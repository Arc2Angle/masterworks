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

public record RenderEquipmentProperty(
        List<Construct.Component.Key> keys,
        Map<Construct.Component.Key, Optional<Dynamic<?>>> arguments,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements RenderProperty {

    @Override
    public Type type() {
        return MasterworksPropertyTypes.RENDER_EQUIPMENT.get();
    }

    public static final class Type extends RenderProperty.Type<RenderEquipmentProperty> {
        @Override
        public Decoder<RenderEquipmentProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return super.decoder(RenderEquipmentProperty::new, components);
        }
    }
}
