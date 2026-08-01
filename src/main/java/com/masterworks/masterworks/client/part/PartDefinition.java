package com.masterworks.masterworks.client.part;

import com.masterworks.masterworks.MasterworksRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.packs.resources.ResourceManager;

public interface PartDefinition {
    public static final Codec<PartDefinition> CODEC =
            MasterworksRegistries.PART_DEFINITION_TYPE.byNameCodec().dispatch(PartDefinition::type, Type::codec);

    Type<?> type();

    PartGeometry geometry(ResourceManager resourceManager);

    record Type<T extends PartDefinition>(MapCodec<T> codec) {}
}
