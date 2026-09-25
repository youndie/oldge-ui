#!/usr/bin/env python3
"""Run source mutants against the tests aimed at them, and say which tests caught each (B-48).

A mutant is a literal in one file replaced by another. It counts as **killed** only when the test it
names in `expect` is among the tests that failed on it. Gradle's exit code is not evidence: a
neighbouring test or a `-Werror` warning fails a build as surely as the aimed test does. That is how
B-19 found a "killed" mutant that its test could never have caught.

Outcomes:
  killed         the expected test failed
  survived       the build passed, or tests failed but not the expected one ("failed elsewhere")
  compile-error  the mutated source did not build; the mutant says nothing about the test
  not-applied    the literal is not in the file (the code moved on; the manifest needs updating)

The file is restored byte for byte after each mutant, and the runner refuses to touch a file with
uncommitted changes, so a crash cannot take somebody's work with it.

    python3 scripts/mutate.py scripts/mutants.json            # every mutant
    python3 scripts/mutate.py scripts/mutants.json --only B-13 # the mutants of one item
"""

from __future__ import annotations

import argparse
import json
import os
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DEFAULT_TASK = ":oldge-core:desktopTest"
DEFAULT_RESULTS = "oldge-core/build/test-results/desktopTest"


@dataclass
class Mutant:
    id: str
    item: str
    file: str
    find: str
    replace: str
    tests: str
    expect: str


@dataclass
class Result:
    mutant: Mutant
    outcome: str
    failed: list[str]


def load(manifest: Path) -> list[Mutant]:
    return [Mutant(**m) for m in json.loads(manifest.read_text(encoding="utf-8"))["mutants"]]


def failed_tests(results: Path) -> list[str] | None:
    """The failed test methods in a JUnit XML directory, or None when there are no results at all."""
    files = sorted(results.glob("TEST-*.xml")) if results.is_dir() else []
    if not files:
        return None
    failed = []
    for f in files:
        for case in ET.parse(f).getroot().iter("testcase"):
            if case.find("failure") is not None or case.find("error") is not None:
                failed.append(f"{case.get('classname')}.{case.get('name')}")
    return failed


def classify(expect: str, exit_code: int, failed: list[str] | None) -> str:
    if failed is None:
        return "compile-error" if exit_code != 0 else "survived"
    if any(expect in name for name in failed):
        return "killed"
    return "survived"


def dirty(path: Path) -> bool:
    out = subprocess.run(["git", "status", "--porcelain", "--", str(path)], cwd=ROOT, capture_output=True, text=True)
    return bool(out.stdout.strip())


def run_one(m: Mutant, command: list[str], results: Path) -> Result:
    path = ROOT / m.file
    original = path.read_bytes()
    text = original.decode("utf-8")
    if m.find not in text:
        return Result(m, "not-applied", [])
    if dirty(path):
        raise SystemExit(f"{m.file} has uncommitted changes; commit or stash them first")
    shutil.rmtree(results, ignore_errors=True)
    path.write_text(text.replace(m.find, m.replace, 1), encoding="utf-8")
    try:
        cmd = [part.replace("{tests}", m.tests) for part in command]
        proc = subprocess.run(cmd, cwd=ROOT, capture_output=True, text=True)
        failed = failed_tests(results)
        return Result(m, classify(m.expect, proc.returncode, failed), failed or [])
    finally:
        path.write_bytes(original)


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("manifest", type=Path)
    ap.add_argument("--only", help="run only the mutants of this item (B-NN) or with this id")
    ap.add_argument(
        "--command",
        help="the test command; {tests} is the mutant's filter (default: ./gradlew <task> --tests {tests} --rerun)",
    )
    ap.add_argument("--results", default=DEFAULT_RESULTS, help="the JUnit XML directory the command writes")
    a = ap.parse_args(argv)
    command = (
        a.command.split()
        if a.command
        else ["./gradlew", DEFAULT_TASK, "--tests", "{tests}", "--rerun", "--console=plain", "-q"]
    )
    results = ROOT / a.results
    mutants = [m for m in load(a.manifest) if not a.only or a.only in (m.item, m.id)]
    if not mutants:
        print("no mutants selected")
        return 1
    outcomes = []
    for m in mutants:
        r = run_one(m, command, results)
        outcomes.append(r)
        caught = ", ".join(n.rsplit(".", 1)[-1] for n in r.failed) or "-"
        print(f"{r.outcome:13} {m.item} {m.id}  (expected {m.expect}; failed: {caught})", flush=True)
    bad = [r for r in outcomes if r.outcome != "killed"]
    print(f"\n{len(outcomes) - len(bad)} of {len(outcomes)} killed")
    return 1 if bad else 0


if __name__ == "__main__":
    os.environ.setdefault("JAVA_HOME", subprocess.run(["/usr/libexec/java_home", "-v", "25"], capture_output=True, text=True).stdout.strip() or os.environ.get("JAVA_HOME", ""))
    sys.exit(main())
