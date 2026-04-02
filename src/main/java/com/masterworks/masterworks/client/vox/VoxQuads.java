package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.MasterworksMod;
import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public final class VoxQuads {
    private static final SpriteId WHITE_PIXEL_SPRITE_ID = new SpriteId(
            Identifier.withDefaultNamespace("textures/atlas/items.png"),
            Identifier.fromNamespaceAndPath(MasterworksMod.ID, "item/white_pixel"));
    private static final int TINT_INDEX = -1;
    private static final boolean SHADE = true;
    private static final int LIGHT_EMISSION = 0;
    private static final boolean HAS_AMBIENT_OCCLUSION = true;

    /**
     * A native 3D dense voxels object.
     *
     * @param colors A dense array of ARGB color values for each voxel in the model.
     *     Invisible voxels (alpha = 0) are considered empty, even if they have
     *     non-zero RGB values.
     * @param count  Represents the number of voxels along each axis.
     * @param size   Represents the size of each voxel in the mesh.
     *
     * @apiNote Sizes and offsets are in minecraft blocks.
     */
    public record Mesh(int[] colors, Vector3ic count, Vector3fc size) {
        public static final Mesh EMPTY = new Mesh(new int[0], new Vector3i(), new Vector3f());

        public QuadCollection bake(Builder builder) {
            int countX = count.x();
            int countY = count.y();
            int countZ = count.z();
            int countXY = countX * countY;

            float sizeX = size.x();
            float sizeY = size.y();
            float sizeZ = size.z();

            for (int i = 0; i < colors.length; i++) {
                if (ARGB.alpha(colors[i]) == 0) {
                    continue;
                }

                BakedColors bakedColors = BakedColors.of(colors[i]);

                float xi = i % countX;
                float yi = (i % countXY) / countX;
                float zi = i / countXY;

                float x0 = sizeX * xi;
                float y0 = sizeY * yi;
                float z0 = sizeZ * zi;

                float x1 = sizeX * (xi + 1);
                float y1 = sizeY * (yi + 1);
                float z1 = sizeZ * (zi + 1);

                if (yi == 0 || ARGB.alpha(colors[i - countX]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x0, y0, z0),
                            new Vector3f(x1, y0, z0),
                            new Vector3f(x1, y0, z1),
                            new Vector3f(x0, y0, z1),
                            Direction.DOWN,
                            bakedColors);
                }

                if (yi == countY - 1 || ARGB.alpha(colors[i + countX]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x0, y1, z1),
                            new Vector3f(x1, y1, z1),
                            new Vector3f(x1, y1, z0),
                            new Vector3f(x0, y1, z0),
                            Direction.UP,
                            bakedColors);
                }

                if (zi == 0 || ARGB.alpha(colors[i - countXY]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x1, y0, z0),
                            new Vector3f(x0, y0, z0),
                            new Vector3f(x0, y1, z0),
                            new Vector3f(x1, y1, z0),
                            Direction.NORTH,
                            bakedColors);
                }

                if (zi == countZ - 1 || ARGB.alpha(colors[i + countXY]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x0, y0, z1),
                            new Vector3f(x1, y0, z1),
                            new Vector3f(x1, y1, z1),
                            new Vector3f(x0, y1, z1),
                            Direction.SOUTH,
                            bakedColors);
                }

                if (xi == 0 || ARGB.alpha(colors[i - 1]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x0, y0, z0),
                            new Vector3f(x0, y0, z1),
                            new Vector3f(x0, y1, z1),
                            new Vector3f(x0, y1, z0),
                            Direction.WEST,
                            bakedColors);
                }

                if (xi == countX - 1 || ARGB.alpha(colors[i + 1]) != 0xFF) {
                    builder.addFace(
                            new Vector3f(x1, y0, z1),
                            new Vector3f(x1, y0, z0),
                            new Vector3f(x1, y1, z0),
                            new Vector3f(x1, y1, z1),
                            Direction.EAST,
                            bakedColors);
                }
            }

            return builder.build();
        }
    }

    public static final class Builder extends QuadCollection.Builder {
        private final BakedQuad.SpriteInfo spriteInfo;
        private final long uv0, uv1, uv2, uv3;

        private Builder(TextureAtlasSprite sprite) {
            spriteInfo = BakedQuad.SpriteInfo.of(new Material.Baked(sprite, false), Transparency.NONE);
            uv0 = packUV(sprite.getU0(), sprite.getV0());
            uv1 = packUV(sprite.getU1(), sprite.getV0());
            uv2 = packUV(sprite.getU1(), sprite.getV1());
            uv3 = packUV(sprite.getU0(), sprite.getV1());
        }

        private static long packUV(float u, float v) {
            return ((long) Float.floatToRawIntBits(u) << 32) | (Float.floatToRawIntBits(v) & 0xFFFFFFFFL);
        }

        public interface Factory {
            Builder create();

            public static Factory ofWhitePixel(SpriteGetter sprites) {
                return new Factory() {
                    TextureAtlasSprite sprite = null;

                    @Override
                    public Builder create() {
                        if (sprite == null) {
                            sprite = sprites.get(WHITE_PIXEL_SPRITE_ID);
                        }

                        return new Builder(sprite);
                    }
                };
            }
        }

        public Builder addFace(
                Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Direction direction, BakedColors bakedColors) {
            BakedQuad quad = new BakedQuad(
                    v1,
                    v2,
                    v3,
                    v4,
                    uv0,
                    uv1,
                    uv2,
                    uv3,
                    TINT_INDEX,
                    direction,
                    spriteInfo,
                    SHADE,
                    LIGHT_EMISSION,
                    BakedNormals.UNSPECIFIED,
                    bakedColors,
                    HAS_AMBIENT_OCCLUSION);
            addCulledFace(direction, quad);
            return this;
        }
    }
}
