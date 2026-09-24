"""Subset DejaVu Sans Condensed (Regular, Bold) into oldge-core's compose resources.

Input: the upstream release `dejavu-fonts-ttf-2.37.zip`
(https://github.com/dejavu-fonts/dejavu-fonts/releases/tag/version_2_37), unpacked; pass its `ttf/`
directory. Needs fontTools (`python3 -m venv .venv && .venv/bin/pip install fonttools`).

Why subset: each upstream file is ~650 KB with 6,253 glyphs, most of them scripts this library
never draws. Why the name stays: the Bitstream Vera licence requires a modified font to be renamed
only if its name contains "Bitstream" or "Vera" (LICENSE, lines 22-26); "DejaVu Sans Condensed"
contains neither. The licence text itself ships beside the font.

The ranges are wider than what the design system's strings use (B-02 derived those by scanning
every preview.html and README.md), because a consumer's text is not the design system's demo copy:
all of Latin-1 and Latin Extended-A, basic Cyrillic plus Ukrainian and Belarusian letters, and the
punctuation, currency and arrow signs a UI plausibly needs. Everything the design system itself
uses is inside them — `DesignStringCoverageTest` holds that.
"""

import pathlib
import sys

from fontTools import subset

RANGES = [
    (0x0020, 0x007E),  # ASCII
    (0x00A0, 0x017F),  # Latin-1 Supplement, Latin Extended-A
    (0x0400, 0x045F),  # Cyrillic, basic block
    (0x0490, 0x0491),  # Ґ ґ
    (0x2010, 0x2027),  # dashes, quotes, bullet, ellipsis
    (0x2030, 0x203A),  # per mille, primes, single guillemets
    (0x20AC, 0x20AC),  # euro
    (0x20BD, 0x20BD),  # rouble
    (0x2116, 0x2116),  # numero
    (0x2190, 0x2195),  # arrows
    (0x2212, 0x2212),  # minus
    (0x2264, 0x2265),  # less / greater than or equal
    (0x2318, 0x2318),  # place of interest (the design's ⌘O menu hint)
    (0x25CF, 0x25CF),  # black circle, a password mask
    (0x2713, 0x2713),  # check mark
]

FACES = {
    "DejaVuSansCondensed.ttf": "dejavu_sans_condensed.ttf",
    "DejaVuSansCondensed-Bold.ttf": "dejavu_sans_condensed_bold.ttf",
}


def main(src: str) -> None:
    out = pathlib.Path(__file__).resolve().parents[2] / "oldge-core/src/commonMain/composeResources/font"
    unicodes = [cp for lo, hi in RANGES for cp in range(lo, hi + 1)]
    for name, target in FACES.items():
        options = subset.Options()
        options.layout_features = ["*"]
        options.name_IDs = ["*"]
        options.name_languages = ["*"]
        options.notdef_outline = True
        options.glyph_names = False
        options.hinting = True
        font = subset.load_font(str(pathlib.Path(src) / name), options)
        subsetter = subset.Subsetter(options)
        subsetter.populate(unicodes=unicodes)
        subsetter.subset(font)
        subset.save_font(font, str(out / target), options)
        print(target, (out / target).stat().st_size, "bytes")


if __name__ == "__main__":
    main(sys.argv[1])
