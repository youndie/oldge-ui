"""Re-vendoring's check and diff (B-36): a manifest from another snapshot fails, and a diff names what moved."""

import json
import shutil
import tempfile
import unittest
from pathlib import Path

import vendor_design_system as v


class VendorDesignSystemTest(unittest.TestCase):
    def setUp(self):
        self.tmp = Path(tempfile.mkdtemp())

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def test_the_committed_references_match_the_snapshot(self):
        self.assertEqual([], v.check())

    def test_a_manifest_rendered_from_another_snapshot_fails(self):
        manifest = self.tmp / "manifest.json"
        data = json.loads(v.MANIFESTS[0].read_text(encoding="utf-8"))
        data["designSystem"]["lastChange"] = "2026-09-23T10:00:00Z"
        manifest.write_text(json.dumps(data), encoding="utf-8")
        wrong = v.check(manifests=[manifest])
        self.assertEqual(1, len(wrong))
        self.assertIn("2026-09-23T10:00:00Z", wrong[0])

    def test_a_diff_names_the_changed_token_the_added_component_and_the_changed_preview(self):
        fetched = self.tmp / "design-system"
        shutil.copytree(v.SNAPSHOT, fetched)
        index = json.loads((fetched / "design-system.json").read_text(encoding="utf-8"))
        index["lastChange"]["at"] = "2026-09-26T09:00:00Z"
        (fetched / "design-system.json").write_text(json.dumps(index), encoding="utf-8")
        tokens = (fetched / "tokens.json").read_text(encoding="utf-8")
        (fetched / "tokens.json").write_text(tokens.replace('"44px"', '"48px"', 1), encoding="utf-8")
        (fetched / "components" / "Knob").mkdir()
        (fetched / "components" / "Knob" / "preview.html").write_text("<!-- @dsCard -->", encoding="utf-8")
        preview = fetched / "components" / "Button" / "preview.html"
        preview.write_text(preview.read_text(encoding="utf-8") + "\n", encoding="utf-8")

        lines = v.diff(fetched)
        self.assertEqual("Design system 2026-09-24T21:57:36Z -> 2026-09-26T09:00:00Z.", lines[0])
        self.assertIn("Tokens changed: hit-min.", lines)
        self.assertIn("Components added: Knob.", lines)
        self.assertIn("preview.html changed: Button.", lines)

    def test_an_unchanged_copy_says_so(self):
        fetched = self.tmp / "design-system"
        shutil.copytree(v.SNAPSHOT, fetched)
        self.assertEqual("No file changed.", v.diff(fetched)[-1])


if __name__ == "__main__":
    unittest.main()
