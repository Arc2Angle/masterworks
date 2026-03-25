package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;

public interface Role {
    public static final Codec<Role> CODEC =
            MasterworksRegistries.ROLE_TYPE.byNameCodec().dispatch(Role::type, Type::codec);

    Type<?> type();

    /**
     * Returns a stream of voxels to render for the given components.
     * @apiNote This is a client-side method and may access client-only resources. It will only be called on the logical client.
     */
    Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components);

    public record Key(Identifier value) {
        public static final Codec<Key> CODEC = Identifier.CODEC.xmap(Key::new, Key::value);

        public static final Key ITEM = new Key(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "item"));
    }

    record Type<T extends Role>(MapCodec<T> codec) {}
}
