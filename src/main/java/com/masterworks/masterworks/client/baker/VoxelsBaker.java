package com.masterworks.masterworks.client.baker;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.vox.Voxels;
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
import org.jspecify.annotations.NonNull;

/**
 * Bakes {@link Voxels} into a list of {@link BakedQuad}s using a single white pixel texture.
 */
public record VoxelsBaker(SpriteGetter sprites) {
    private static final SpriteId WHITE_PIXEL_SPRITE_ID = new SpriteId(
            Identifier.withDefaultNamespace("textures/atlas/items.png"),
            Identifier.fromNamespaceAndPath(MasterworksMod.ID, "item/white_pixel"));

    private static final int TINT_INDEX = -1;
    private static final boolean SHADE = true;
    private static final int LIGHT_EMMISION = 0;
    private static final boolean HAS_AMBIANT_OCCLUSION = true;

    public QuadCollection bake(@NonNull Voxels voxels) {
        Builder builder = new Builder(sprites.get(WHITE_PIXEL_SPRITE_ID));

        int[] colors = voxels.colors();

        int countX = voxels.count().x;
        int countY = voxels.count().y;
        int countZ = voxels.count().z;
        int countXY = countX * countY;

        Vector3f size = voxels.size();
        Vector3f offset = voxels.offset();

        for (int i = 0; i < colors.length; i++) {
            if (ARGB.alpha(colors[i]) == 0) {
                continue;
            }

            BakedColors bakedColors = BakedColors.of(colors[i]);

            float xi = i % countX;
            float yi = (i % countXY) / countX;
            float zi = i / countXY;

            float x0 = size.x * xi / countX + offset.x;
            float y0 = size.y * yi / countY + offset.y;
            float z0 = size.z * zi / countZ + offset.z;

            float x1 = size.x * (xi + 1) / countX + offset.x;
            float y1 = size.y * (yi + 1) / countY + offset.y;
            float z1 = size.z * (zi + 1) / countZ + offset.z;

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

    private final class Builder extends QuadCollection.Builder {
        private final BakedQuad.SpriteInfo spriteInfo;
        private final long uv0, uv1, uv2, uv3;

        public Builder(TextureAtlasSprite sprite) {
            spriteInfo = BakedQuad.SpriteInfo.of(new Material.Baked(sprite, false), Transparency.NONE);
            uv0 = packUV(sprite.getU0(), sprite.getV0());
            uv1 = packUV(sprite.getU1(), sprite.getV0());
            uv2 = packUV(sprite.getU1(), sprite.getV1());
            uv3 = packUV(sprite.getU0(), sprite.getV1());
        }

        private static long packUV(float u, float v) {
            return ((long) Float.floatToRawIntBits(u) << 32) | (Float.floatToRawIntBits(v) & 0xFFFFFFFFL);
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
                    LIGHT_EMMISION,
                    BakedNormals.UNSPECIFIED,
                    bakedColors,
                    HAS_AMBIANT_OCCLUSION);
            addCulledFace(direction, quad);
            return this;
        }
    }
}
