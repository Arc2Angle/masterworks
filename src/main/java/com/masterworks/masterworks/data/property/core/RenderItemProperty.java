package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.RenderProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record RenderItemProperty(
        List<Construct.Component.Key> keys,
        Map<Construct.Component.Key, Optional<Dynamic<?>>> arguments,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements RenderProperty {

    @Override
    public Type type() {
        return MasterworksPropertyTypes.RENDER_ITEM.get();
    }

    public static final class Type extends RenderProperty.Type<RenderItemProperty> {
        @Override
        public Decoder<RenderItemProperty> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return super.decoder(RenderItemProperty::new, components);
        }
    }
}
