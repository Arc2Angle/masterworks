package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public interface Role {
    public static final Codec<Role> CODEC =
            MasterworksRegistries.ROLE_TYPE.byNameCodec().dispatch(Role::type, Type::codec);

    Type<?> type();

    Property.Container properties(Construct construct, Construct.Component.Key key);

    Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components);

    public record Key(ResourceLocation value) {
        public static final Codec<Key> CODEC = ResourceLocation.CODEC.xmap(Key::new, Key::value);

        public static final Key ITEM = new Key(ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "item"));
    }

    record Type<T extends Role>(MapCodec<T> codec) {}
}
