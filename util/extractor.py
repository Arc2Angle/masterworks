from __future__ import annotations

from typing import NamedTuple

import argparse
import sys
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


def process_image(input_path: str, shape_path: str, palette_path: str, strict: bool) -> None:
    image = Image.open(input_path).convert("RGBA")
    width, height = image.size

    pixels = [RGBA.from_image(image.getpixel((i, j))) for j in range(height) for i in range(width)]

    try:
        palette = Image.open(palette_path).convert("RGBA")
        if palette.size != (8, 1):
            raise ValueError("Palette image must be exactly 8x1 pixels.")

    except FileNotFoundError:
        unique_colors = set(pixel for pixel in pixels if pixel.a != 0)

        if len(unique_colors) > 8:
            raise ValueError(
                f"The image contains more than 8 unique colors (ignoring transparency), found {len(unique_colors)}"
            )

        if strict and len(unique_colors) != 8:
            raise ValueError(
                f"The image must contain exactly 8 unique colors (ignoring transparency), not {len(unique_colors)}"
            )

        # Grayscale formula: (0xFF - intensity) / 0x24
        # Index 0 results in Intensity 255 (White/Light)
        # Index 7 results in Intensity 3 (Black/Dark)
        # Therefore, Index 0 must be the lightest color in the palette.
        sorted_colors = sorted(list(unique_colors), key=RGBA.get_luminance, reverse=True)

        palette = Image.new("RGBA", (8, 1))
        palette.putdata(sorted_colors)
        palette.save(palette_path)
        print(f"Generated {'partial' if len(unique_colors) < 8 else 'complete'} palette: {palette_path}")

    else:
        sorted_colors = [RGBA.from_image(palette.getpixel((i, 0))) for i in range(8)]
        print(f"Using existing palette: {palette_path}")

    # Map each color to its calculated Palette Index (0-7)
    # We need to reverse the formula:
    # Java: index = (255 - intensity) / 36
    # Python: intensity = 255 - (index * 36)
    intensity_steps = {color: 255 - i * 36 for i, color in enumerate(sorted_colors)}
    grayscale = [
        (
            RGBA.grayscale_from_intensity(intensity, pixel.a)
            if (intensity := intensity_steps.get(pixel)) is not None
            else RGBA(0, 0, 0, 0)
        )
        for pixel in pixels
    ]

    shape = Image.new("RGBA", (width, height))
    shape.putdata(grayscale)
    shape.save(shape_path)
    print(f"Generated shape: {shape_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Extract grayscale shape and palette from Minecraft item texture.")
    parser.add_argument("input", help="Path to input image (e.g., diamond_pickaxe.png)")
    parser.add_argument("--shape", default="shape.png", help="Output path for grayscale shape")
    parser.add_argument("--palette", default="palette.png", help="Output path for palette, or existing palette to use")
    parser.add_argument("--strict", action="store_true", help="Enable strict mode for new palette validation")

    args = parser.parse_args()

    process_image(args.input, args.shape, args.palette, args.strict)
