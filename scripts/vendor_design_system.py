#!/usr/bin/env python3
"""
Re-vendoring the design system (B-36).

    python3 scripts/vendor_design_system.py check              # in make check: references match the snapshot
    python3 scripts/vendor_design_system.py diff  <fetched>    # what a fetched copy changes, for the commit body
    python3 scripts/vendor_design_system.py apply <fetched>    # replace reference/design-system/ with it

The design system is edited live in its claude.ai artifact, and `reference/design-system/` is a
snapshot of it that the tokens, the icons and every parity reference are generated from. A silent
overwrite would move all three at once and nothing would say which change came from where. So
re-vendoring is a branch of its own, and this script owns the three steps a machine can do.

FETCHING is not here. It needs the signed-in Artifact tool, which a script cannot hold, and the
rejected alternative (fetching at build time) would make the build depend on a claude.ai session
and let a reference change without a commit. `CLAUDE.md`, "Re-vendoring the design system", says
how the files are fetched into a scratch directory.

CHECK holds every reference manifest to the snapshot: its `designSystem.lastChange` must equal
`design-system.json`'s `lastChange.at`. References rendered from an older snapshot, or a snapshot
replaced without re-rendering, fail `make check`.
"""
import argparse
import filecmp
import json
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SNAPSHOT = ROOT / "reference/design-system"
MANIFESTS = [
    ROOT / "oldge-core/src/desktopTest/snapshots/design/manifest.json",
    ROOT / "sample/src/desktopTest/snapshots/design/manifest.json",
]


def snapshot_version(root=SNAPSHOT):
    return json.loads((root / "design-system.json").read_text(encoding="utf-8"))["lastChange"]["at"]


def shown(path):
    """A path relative to the repository where it is inside it."""
    return path.relative_to(ROOT) if path.is_relative_to(ROOT) else path


def check(snapshot=SNAPSHOT, manifests=MANIFESTS):
    """The mismatched manifests, as messages; empty when every one names the snapshot."""
    want = snapshot_version(snapshot)
    wrong = []
    for manifest in manifests:
        got = json.loads(manifest.read_text(encoding="utf-8")).get("designSystem", {}).get("lastChange")
        if got != want:
            wrong.append(
                f"{shown(manifest)}: rendered from the design system of {got}, "
                f"the snapshot is {want}; re-render (make references, and --out for sample's pages)"
            )
    return wrong


def tokens(root):
    """Every token of `tokens.json` by its name: its value (or per-theme values)."""
    found = {}

    def walk(node):
        if isinstance(node, dict):
            if "name" in node and ("value" in node or "values" in node or "themes" in node):
                found[node["name"]] = json.dumps({k: v for k, v in node.items() if k in ("value", "values", "themes")}, sort_keys=True)
                return
            for v in node.values():
                walk(v)
        elif isinstance(node, list):
            for v in node:
                walk(v)

    walk(json.loads((root / "tokens.json").read_text(encoding="utf-8")))
    return found


def components(root):
    return {d.name for d in (root / "components").iterdir() if d.is_dir()}


def diff(fetched, snapshot=SNAPSHOT):
    """What `fetched` changes against the snapshot, as the lines of a commit body."""
    lines = [f"Design system {snapshot_version(snapshot)} -> {snapshot_version(fetched)}."]
    old, new = tokens(snapshot), tokens(fetched)
    for label, names in (
        ("Tokens added", sorted(new.keys() - old.keys())),
        ("Tokens removed", sorted(old.keys() - new.keys())),
        ("Tokens changed", sorted(n for n in old.keys() & new.keys() if old[n] != new[n])),
    ):
        if names:
            lines.append(f"{label}: {', '.join(names)}.")
    was, now = components(snapshot), components(fetched)
    for label, names in (("Components added", sorted(now - was)), ("Components removed", sorted(was - now))):
        if names:
            lines.append(f"{label}: {', '.join(names)}.")
    changed = {}
    for name in sorted(was & now):
        for f in ("preview.html", "README.md"):
            a, b = snapshot / "components" / name / f, fetched / "components" / name / f
            if a.is_file() != b.is_file() or (a.is_file() and not filecmp.cmp(a, b, shallow=False)):
                changed.setdefault(f, []).append(name)
    for f, names in changed.items():
        lines.append(f"{f} changed: {', '.join(names)}.")
    for shared in ("components/bundle.css", "components/bundle.js", "components/index.d.ts", "README.md"):
        a, b = snapshot / shared, fetched / shared
        if a.is_file() != b.is_file() or (a.is_file() and not filecmp.cmp(a, b, shallow=False)):
            lines.append(f"{shared} changed.")
    if len(lines) == 1:
        lines.append("No file changed.")
    return lines


def apply(fetched, snapshot=SNAPSHOT):
    """Replace the snapshot with `fetched` entirely, so a removed file is removed too."""
    shutil.rmtree(snapshot)
    shutil.copytree(fetched, snapshot)


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="command", required=True)
    sub.add_parser("check")
    for name in ("diff", "apply"):
        p = sub.add_parser(name)
        p.add_argument("fetched", type=Path)
    a = ap.parse_args(argv)
    if a.command == "check":
        wrong = check()
        for w in wrong:
            print(f"vendor_design_system: {w}", file=sys.stderr)
        if not wrong:
            print(f"references match the design system of {snapshot_version()}")
        return 1 if wrong else 0
    if not (a.fetched / "design-system.json").is_file():
        print(f"vendor_design_system: {a.fetched} has no design-system.json", file=sys.stderr)
        return 1
    if a.command == "diff":
        print("\n".join(diff(a.fetched)))
        return 0
    apply(a.fetched)
    print(f"reference/design-system/ replaced with {a.fetched}; now regenerate tokens, icons and references")
    return 0


if __name__ == "__main__":
    sys.exit(main())
