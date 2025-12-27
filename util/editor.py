from __future__ import annotations

from typing import NamedTuple

import argparse
from PIL import Image


class RGBA(NamedTuple):
    r: int
    g: int
    b: int
    a: int

    @classmethod
    def from_image(cls, value: float | tuple[int, ...] | None) -> RGBA:
        if type(value) is not tuple or len(value) != 4:
            raise ValueError("Image pixels must be RGBA tuples.")
        return cls(*value)

    @classmethod
    def grayscale_from_intensity(cls, intensity: int, alpha: int) -> RGBA:
        return cls(intensity, intensity, intensity, alpha)

    def get_luminance(self) -> float:
        """Calculates relative luminance of a pixel to help sort light->dark."""
        return 0.299 * self.r + 0.587 * self.g + 0.114 * self.b


def process_image(input_path: str, output_path: str, map_indices: dict[int, int]) -> None:
    image = Image.open(input_path).convert("RGBA")
    width, height = image.size

    pixels = [RGBA.from_image(image.getpixel((i, j))) for j in range(height) for i in range(width)]
    unique_colors = set(pixel for pixel in pixels if pixel.a != 0)
    sorted_colors = sorted(unique_colors, key=RGBA.get_luminance, reverse=True)

    map_colors = {sorted_colors[k]: sorted_colors[v] for k, v in map_indices.items()}

    image.putdata([map_colors.get(pixel, pixel) for pixel in pixels])
    image.save(output_path)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Remap colors in an image based on provided indices.")
    parser.add_argument("input", type=str, help="Path to the input image file.")
    parser.add_argument("output", type=str, help="Path to save the output image file")
    parser.add_argument(
        "--map",
        type=str,
        nargs="+",
        required=True,
        help="List of index mappings in the form of 'from to' pairs. Example: --map 0:1 1:1 2:3",
    )

    args = parser.parse_args()
    process_image(args.input, args.output, {int(k): int(v) for k, v in (map_pair.split(":") for map_pair in args.map)})
