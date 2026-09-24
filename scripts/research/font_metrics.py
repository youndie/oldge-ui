"""Re-run research §1.6's font measurement on the files this repository bundles.

Width = the sum of HarfBuzz advances (default features, kerning on) over unitsPerEm, i.e. the
string's width in em at one font size; x-height and cap height = the top of the outlines of `x`
and `H`. The targets are the Microsoft faces on this mac, which the design names and this
repository may not ship; they are read, never copied.

    python3 -m venv .venv && .venv/bin/pip install fonttools uharfbuzz
    .venv/bin/python scripts/research/font_metrics.py
"""

import pathlib

import uharfbuzz as hb
from fontTools.pens.boundsPen import BoundsPen
from fontTools.ttLib import TTFont

ROOT = pathlib.Path(__file__).resolve().parents[2]
FONTS = ROOT / "oldge-core/src/commonMain/composeResources/font"
SYSTEM = pathlib.Path("/System/Library/Fonts/Supplemental")
RU = "Синхронизировать при подключении Создать резервную копию"
LA = "The quick brown fox jumps over the lazy dog 0123456789"

GROUPS = {
    "ui 400": [(SYSTEM / "Tahoma.ttf", "Tahoma"), (FONTS / "dejavu_sans_condensed.ttf", "DejaVu Sans Condensed")],
    "ui 700": [
        (SYSTEM / "Tahoma Bold.ttf", "Tahoma Bold"),
        (FONTS / "dejavu_sans_condensed_bold.ttf", "DejaVu Sans Condensed Bold"),
    ],
    "title 700": [(SYSTEM / "Trebuchet MS Bold.ttf", "Trebuchet MS Bold"), (FONTS / "fira_sans_bold.ttf", "Fira Sans Bold")],
}


def width(data, text):
    face = hb.Face(data)
    font = hb.Font(face)
    buf = hb.Buffer()
    buf.add_str(text)
    buf.guess_segment_properties()
    hb.shape(font, buf, {})
    return sum(p.x_advance for p in buf.glyph_positions) / face.upem


def top(font, ch):
    glyphs = font.getGlyphSet()
    pen = BoundsPen(glyphs)
    glyphs[font.getBestCmap()[ord(ch)]].draw(pen)
    return pen.bounds[3] / font["head"].unitsPerEm


def main():
    for group, faces in GROUPS.items():
        print(f"\n## {group}\n| font | RU em | ΔRU | LA em | ΔLA | x-h | cap | x/cap |\n|---|---|---|---|---|---|---|---|")
        ref = None
        for path, name in faces:
            if not path.exists():
                print(f"| {name} | not on this machine | | | | | | |")
                continue
            data = path.read_bytes()
            font = TTFont(str(path))
            ru, la, xh, cap = width(data, RU), width(data, LA), top(font, "x"), top(font, "H")
            ref = ref or (ru, la)
            print(
                f"| {name} | {ru:.2f} | {100 * (ru / ref[0] - 1):+.1f}% | {la:.2f} | {100 * (la / ref[1] - 1):+.1f}% "
                f"| {xh:.3f} | {cap:.3f} | {xh / cap:.3f} |",
            )


if __name__ == "__main__":
    main()
