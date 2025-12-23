package com.masterworks.masterworks;

import java.util.function.Function;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.base.LoreComponentProperty;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import com.masterworks.masterworks.util.tags.PermissiveTypedTagKey;
import com.masterworks.masterworks.util.tags.TypedTagKey;
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



    public static final TypedTagKey<Property.Type<?>, DataComponentProperty.Type<?>> DATA_COMPONENT_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "data_components",
                    value -> (DataComponentProperty.Type<?>) value);

    public static final TypedTagKey<Property.Type<?>, LoreComponentProperty.Type<?>> LORE_COMPONENT_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "lore_components",
                    value -> (LoreComponentProperty.Type<?>) value);

    public static final TypedTagKey<Property.Type<?>, ItemAttributeModifierProperty.Type<?>> ITEM_ATTRIBUTE_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "item_attributes",
                    value -> (ItemAttributeModifierProperty.Type<?>) value);

    public static final TypedTagKey<Property.Type<?>, ToolRuleProperty.Type<?>> TOOL_RULE_PROPERTY_TYPES =
            createWarningTyped(MasterworksRegistries.PROPERTY_TYPE, "tool_rules",
                    value -> (ToolRuleProperty.Type<?>) value);
}
