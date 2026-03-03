from __future__ import annotations

from typing import NamedTuple

import argparse
from PIL import Image


def process_image(path: str) -> None:
    input = Image.open(path).convert("RGBA")
    width, height = input.size

    if width < 8 or height != 1:
        raise ValueError("Image must be at least 8 pixels wide and exactly 1 pixel tall.")

    output = Image.new("RGBA", (8, 1))
    output.putdata([input.getpixel((i, 0)) for i in range(8)])  # type: ignore
    output.save(path)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Cut image to exactly 8x1.")
    parser.add_argument("path", type=str, help="Path to the image file.")

    args = parser.parse_args()
    process_image(args.path)
