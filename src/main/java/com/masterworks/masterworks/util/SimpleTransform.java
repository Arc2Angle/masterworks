package com.masterworks.masterworks.util;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.TransformationHelper;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A simple transform that can be both encoded and decoded, mimicking {@link ItemTransform}.
 * @param rotation The rotation xyz in degrees
 * @param translation The translation xyz in pixels
 * @param scale The scale xyz
 */
public record SimpleTransform(
        Optional<Vector3fc> rotation, Optional<Vector3fc> translation, Optional<Vector3fc> scale) {
    public static final Codec<SimpleTransform> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.VECTOR3F.optionalFieldOf("rotation").forGetter(SimpleTransform::rotation),
                    ExtraCodecs.VECTOR3F.optionalFieldOf("translation").forGetter(SimpleTransform::translation),
                    ExtraCodecs.VECTOR3F.optionalFieldOf("scale").forGetter(SimpleTransform::scale))
            .apply(instance, SimpleTransform::new));

    public static final float PIXEL_SCALE = 1.0f / 16.0f;

    public static final SimpleTransform EMPTY =
            new SimpleTransform(Optional.empty(), Optional.empty(), Optional.empty());

    public Transformation complete() {
        return new Transformation(
                translation.map(xyz -> xyz.mul(PIXEL_SCALE, new Vector3f())).orElse(null),
                rotation.map(xyz -> TransformationHelper.quatFromXYZ(new Vector3f(xyz), true))
                        .orElse(null),
                scale.orElse(null),
                null);
    }
}
