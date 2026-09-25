"""The catalogue's check (B-42): it fails on a hand edit, on a stale map, and lists every public composable."""

import shutil
import tempfile
import unittest
from pathlib import Path

import component_catalog as cat


class ComponentCatalogTest(unittest.TestCase):
    def setUp(self):
        self.saved = (cat.CATALOG, dict(cat.INSTEAD), dict(cat.ALSO))

    def tearDown(self):
        cat.CATALOG, instead, also = self.saved
        cat.INSTEAD.clear()
        cat.INSTEAD.update(instead)
        cat.ALSO.clear()
        cat.ALSO.update(also)

    def test_the_committed_catalogue_is_up_to_date(self):
        self.assertEqual(0, cat.main(["--check"]))

    def test_editing_one_row_by_hand_fails_the_check(self):
        tmp = Path(tempfile.mkdtemp()) / "components.md"
        shutil.copy(cat.CATALOG, tmp)
        text = tmp.read_text(encoding="utf-8")
        edited = text.replace("| Button | `OldgeButton`", "| Button | `OldgeButtonEdited`", 1)
        self.assertNotEqual(text, edited, "the row to edit was not found")
        tmp.write_text(edited, encoding="utf-8")
        cat.CATALOG = tmp
        self.assertEqual(1, cat.main(["--check"]))

    def test_a_map_entry_that_outlived_a_rename_is_an_error(self):
        cat.INSTEAD["Chip"] = ["OldgeAssistChip", "OldgeGoneChip"]
        _, errors = cat.generate()
        self.assertEqual(["Chip: no public composable OldgeGoneChip"], errors)

    def test_every_public_composable_is_in_a_table(self):
        block, errors = cat.generate()
        self.assertEqual([], errors)
        missing = [n for n in cat.public_composables(cat.CORE) if f"`{n}`" not in block]
        self.assertEqual([], missing)

    def test_a_kdoc_that_does_not_name_its_component_is_an_error(self):
        # B-45: a tree whose OldgeMeter's KDoc drops the design system's `Meter` fails, and one
        # whose public function has no KDoc at all fails too.
        # Under the repository: the catalogue names each file by its path relative to the root.
        (cat.ROOT / "build").mkdir(exist_ok=True)
        scratch = Path(tempfile.mkdtemp(dir=cat.ROOT / "build"))
        self.addCleanup(shutil.rmtree, scratch, True)
        tree = scratch / "kotlin"
        shutil.copytree(cat.CORE, tree)
        meter = next(tree.rglob("OldgeMeter.kt"))
        text = meter.read_text(encoding="utf-8")
        edited = text.replace("the design system's `Meter`", "a meter", 1)
        self.assertNotEqual(text, edited, "the KDoc to edit was not found")
        meter.write_text(edited, encoding="utf-8")
        gloss = next(tree.rglob("OldgeGloss.kt"))
        text = gloss.read_text(encoding="utf-8")
        cut = text.index("/**\n * [gloss] as the element's background")
        gloss.write_text(text[:cut] + text[text.index("*/", cut) + 3 :], encoding="utf-8")
        saved = cat.CORE
        try:
            cat.CORE = tree
            _, errors = cat.generate()
        finally:
            cat.CORE = saved
        self.assertIn("OldgeMeter: its KDoc does not name the design system's `Meter` it implements", errors)
        self.assertIn("oldgeGloss: a public function without a KDoc", errors)


if __name__ == "__main__":
    unittest.main()
