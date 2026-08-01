package com.masterworks.masterworks.data.composition;

import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.client.renderer.builder.RendererBuilder;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.masterworks.masterworks.data.property.Property;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;

public interface Composition {
    static final Codec<Composition> CODEC =
            MasterworksRegistries.COMPOSITION_TYPE.byNameCodec().dispatch(Composition::type, Type::codec);
    static final String COMPONENTS_KEY = "components", ROLES_KEY = "roles", PROPERTIES_KEY = "properties";

    Type<?> type();

    List<Construct.Component.Key> components();

    Map<Role, ? extends RendererBuilder> roles();

    Property.Container properties();

    record Type<T extends Composition>(MapCodec<T> codec) {}
}
