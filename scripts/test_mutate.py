"""The mutation runner against a stub build (B-48): a mutant it must report killed, one it must
report surviving, one that fails to compile, and one whose literal is gone."""

import json
import sys
import tempfile
import textwrap
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import mutate  # noqa: E402

# A stand-in for Gradle: reads the "source", "compiles" it, and writes JUnit XML the way the test task
# does. The source says which tests fail through the markers in it.
STUB = textwrap.dedent(
    """
    import pathlib, sys
    src = pathlib.Path(sys.argv[1]).read_text()
    out = pathlib.Path(sys.argv[2]); out.mkdir(parents=True, exist_ok=True)
    if "SYNTAX" in src:
        sys.exit(1)
    cases = {"aimed": "BREAK_AIMED" in src, "other": "BREAK_OTHER" in src}
    xml = ['<testsuite name="Stub">']
    for name, broken in cases.items():
        xml.append(f'<testcase classname="Stub" name="{name}()">' + ('<failure/>' if broken else '') + '</testcase>')
    xml.append('</testsuite>')
    (out / "TEST-Stub.xml").write_text("".join(xml))
    sys.exit(1 if any(cases.values()) else 0)
    """
)


class MutateTest(unittest.TestCase):
    def setUp(self):
        # Outside the repository, with the uncommitted-changes guard stubbed: the test touches no index.
        self.dir = Path(tempfile.mkdtemp())
        self.src = self.dir / "Source.kt"
        self.src.write_text("val x = OK\n")
        self.stub = self.dir / "stub.py"
        self.stub.write_text(STUB)
        self.results = self.dir / "results"
        self.real_dirty = mutate.dirty
        mutate.dirty = lambda path: False

    def tearDown(self):
        mutate.dirty = self.real_dirty

    def run_mutant(self, replace):
        rel = str(self.src)  # absolute: ROOT / an absolute path is that path
        manifest = self.dir / "mutants.json"
        manifest.write_text(
            json.dumps(
                {
                    "mutants": [
                        {"id": "m", "item": "B-00", "file": rel, "find": "OK", "replace": replace, "tests": "*", "expect": "aimed"}
                    ]
                }
            )
        )
        m = mutate.load(manifest)[0]
        command = [sys.executable, str(self.stub), str(self.src), str(self.results)]
        result = mutate.run_one(m, command, self.results)
        self.assertEqual("val x = OK\n", self.src.read_text(), "the source is restored")
        return result.outcome

    def test_the_aimed_test_failing_is_a_kill(self):
        self.assertEqual("killed", self.run_mutant("BREAK_AIMED"))

    def test_a_passing_build_is_a_survivor(self):
        self.assertEqual("survived", self.run_mutant("STILL_FINE"))

    def test_another_test_failing_is_not_a_kill(self):
        self.assertEqual("survived", self.run_mutant("BREAK_OTHER"))

    def test_a_build_that_does_not_compile_is_its_own_outcome(self):
        self.assertEqual("compile-error", self.run_mutant("SYNTAX"))

    def test_a_literal_that_is_gone_is_reported(self):
        self.src.write_text("val x = MOVED\n")
        m = mutate.Mutant("m", "B-00", str(self.src), "OK", "X", "*", "aimed")
        self.assertEqual("not-applied", mutate.run_one(m, ["true"], self.results).outcome)


if __name__ == "__main__":
    unittest.main()
