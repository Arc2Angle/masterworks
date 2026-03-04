package com.masterworks.masterworks.client.baker;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.registrar.AtlasStitchDependenetsRegistrar;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.platform.Transparency;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad.SpriteInfo;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
public record VoxelsBaker(
        SpriteInfo whitePixelSpriteInfo, long packedUV0, long packedUV1, long packedUV2, long packedUV3) {

    public record Factory() implements AtlasStitchDependenetsRegistrar.Factory<VoxelsBaker> {
        @Override
        public Identifier atlasId() {
            return Identifier.withDefaultNamespace("textures/atlas/items.png");
        }

        private static final Identifier SPRITE_ID =
                Identifier.fromNamespaceAndPath(MasterworksMod.ID, "item/white_pixel");

        @Override
        public VoxelsBaker create(TextureAtlas atlas) {
            TextureAtlasSprite sprite = atlas.getSprite(SPRITE_ID);
            SpriteInfo spriteInfo = SpriteInfo.of(new Material.Baked(sprite, false), Transparency.NONE);

            long packedUV0 = packUV(sprite.getU0(), sprite.getV0());
            long packedUV1 = packUV(sprite.getU1(), sprite.getV0());
            long packedUV2 = packUV(sprite.getU1(), sprite.getV1());
            long packedUV3 = packUV(sprite.getU0(), sprite.getV1());

            return new VoxelsBaker(spriteInfo, packedUV0, packedUV1, packedUV2, packedUV3);
        }

        private static long packUV(float u, float v) {
            return ((long) Float.floatToRawIntBits(u) << 32) | (Float.floatToRawIntBits(v) & 0xFFFFFFFFL);
        }
    }

    public List<BakedQuad> bake(@NonNull Voxels voxels) {
        List<BakedQuad> quads = new ArrayList<>();

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
                quads.add(createQuad(
                        new Vector3f(x0, y0, z0),
                        new Vector3f(x1, y0, z0),
                        new Vector3f(x1, y0, z1),
                        new Vector3f(x0, y0, z1),
                        Direction.DOWN,
                        bakedColors));
            }

            if (yi == countY - 1 || ARGB.alpha(colors[i + countX]) != 0xFF) {
                quads.add(createQuad(
                        new Vector3f(x0, y1, z1),
                        new Vector3f(x1, y1, z1),
                        new Vector3f(x1, y1, z0),
                        new Vector3f(x0, y1, z0),
                        Direction.UP,
                        bakedColors));
            }

            if (zi == 0 || ARGB.alpha(colors[i - countXY]) != 0xFF) {
                quads.add(createQuad(
                        new Vector3f(x1, y0, z0),
                        new Vector3f(x0, y0, z0),
                        new Vector3f(x0, y1, z0),
                        new Vector3f(x1, y1, z0),
                        Direction.NORTH,
                        bakedColors));
            }

            if (zi == countZ - 1 || ARGB.alpha(colors[i + countXY]) != 0xFF) {
                quads.add(createQuad(
                        new Vector3f(x0, y0, z1),
                        new Vector3f(x1, y0, z1),
                        new Vector3f(x1, y1, z1),
                        new Vector3f(x0, y1, z1),
                        Direction.SOUTH,
                        bakedColors));
            }

            if (xi == 0 || ARGB.alpha(colors[i - 1]) != 0xFF) {
                quads.add(createQuad(
                        new Vector3f(x0, y0, z0),
                        new Vector3f(x0, y0, z1),
                        new Vector3f(x0, y1, z1),
                        new Vector3f(x0, y1, z0),
                        Direction.WEST,
                        bakedColors));
            }

            if (xi == countX - 1 || ARGB.alpha(colors[i + 1]) != 0xFF) {
                quads.add(createQuad(
                        new Vector3f(x1, y0, z1),
                        new Vector3f(x1, y0, z0),
                        new Vector3f(x1, y1, z0),
                        new Vector3f(x1, y1, z1),
                        Direction.EAST,
                        bakedColors));
            }
        }

        return quads;
    }

    public static final int TINT_INDEX = -1;
    public static final boolean SHADE = true;
    public static final int LIGHT_EMMISION = 0;
    public static final boolean HAS_AMBIANT_OCCLUSION = true;

    private BakedQuad createQuad(
            Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Direction direction, BakedColors bakedColors) {
        return new BakedQuad(
                v1,
                v2,
                v3,
                v4,
                packedUV0,
                packedUV1,
                packedUV2,
                packedUV3,
                TINT_INDEX,
                direction,
                whitePixelSpriteInfo,
                SHADE,
                LIGHT_EMMISION,
                BakedNormals.UNSPECIFIED,
                bakedColors,
                HAS_AMBIANT_OCCLUSION);
    }
}
