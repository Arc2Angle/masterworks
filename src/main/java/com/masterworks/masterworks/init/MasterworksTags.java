package com.masterworks.masterworks.init;

import java.util.function.Function;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.provider.DataComponentProviderProperty;
import com.masterworks.masterworks.data.property.provider.ItemAttributeProviderProperty;
import com.masterworks.masterworks.data.property.provider.ToolRuleProviderProperty;
import com.masterworks.masterworks.init.tag.PermissiveTypedTagKey;
import com.masterworks.masterworks.init.tag.TypedTagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class MasterworksTags {

    static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> key, String path) {
        return TagKey.create(key, ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, path));
    }

    static <T, U extends T> TypedTagKey<T, U> createWarningTyped(Registry<T> registry, String path,
            Function<? super T, ? extends U> cast) {
        TagKey<T> untyped = TagKey.create(registry.key(),
                ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, path));

        return PermissiveTypedTagKey.createSuppressing(untyped, registry, cast, exception -> {
            MasterworksMod.LOGGER.warn("Error loading value of tag {}: {}", untyped.location(),
                    untyped.location(), exception.getMessage());
        });
    }



    public static final TypedTagKey<Property.Type<?>, DataComponentProviderProperty.Type<?, ?>> DATA_COMPONENT_PROVIDER_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "data_component_providers",
                    value -> (DataComponentProviderProperty.Type<?, ?>) value);

    public static final TypedTagKey<Property.Type<?>, ItemAttributeProviderProperty.Type<?>> ITEM_ATTRIBUTE_PROVIDER_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "item_attribute_providers",
                    value -> (ItemAttributeProviderProperty.Type<?>) value);

    public static final TypedTagKey<Property.Type<?>, ToolRuleProviderProperty.Type<?>> TOOL_RULE_PROVIDER_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "tool_rule_providers",
                    value -> (ToolRuleProviderProperty.Type<?>) value);
}
