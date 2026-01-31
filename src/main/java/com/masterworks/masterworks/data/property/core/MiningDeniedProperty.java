package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

public sealed interface MiningDeniedProperty extends ToolRuleProperty
        permits MiningDeniedProperty.Direct, MiningDeniedProperty.Reference {
    TagKey<Block> blocks(Map<Construct.Component.Key, Construct.Component> components);

    @Override
    default Tool.Rule getToolRule(Construct construct) {
        TagKey<Block> tagKey = blocks(construct.components());
        HolderSet.Named<Block> set = BuiltInRegistries.BLOCK
                .get(tagKey)
                .orElseThrow(() -> new IllegalStateException("Block tag " + tagKey + " not found"));
        return Tool.Rule.deniesDrops(set);
    }

    @Override
    default Type type() {
        return MasterworksPropertyTypes.MINING_DENIED.get();
    }

    public static final class Type implements ToolRuleProperty.Type<MiningDeniedProperty> {
        @Override
        public Decoder<MiningDeniedProperty> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<>() {
                @Override
                public <T> DataResult<MiningDeniedProperty> decode(Dynamic<T> input) {
                    return Codec.STRING
                            .parse(input)
                            .flatMap(argument -> {
                                if (!argument.startsWith(ExpressionProperty.ARGUMENT_PREFIX)) {
                                    return DataResult.error(() ->
                                            "Invalid mining denied argument (must be prefixed with $): " + argument);
                                }

                                Construct.Component.Key key = new Construct.Component.Key(
                                        argument.substring(ExpressionProperty.ARGUMENT_PREFIX.length()));

                                return DataResult.success(key);
                            })
                            .mapOrElse(
                                    key -> {
                                        RoleReferenceLocation role = components.get(key);
                                        if (role == null) {
                                            return DataResult.error(() -> "Missing component: " + key);
                                        }

                                        return DataResult.success(new Reference(key, role));
                                    },
                                    error -> TagKey.codec(Registries.BLOCK)
                                            .parse(input)
                                            .flatMap(tagKey -> {
                                                return DataResult.success(new Direct(tagKey));
                                            }));
                }
            });
        }
    }

    record Direct(TagKey<Block> tagKey) implements MiningDeniedProperty {
        @Override
        public TagKey<Block> blocks(Map<Construct.Component.Key, Construct.Component> components) {
            return tagKey;
        }
    }

    record Reference(Construct.Component.Key key, RoleReferenceLocation role) implements MiningDeniedProperty {
        @Override
        public TagKey<Block> blocks(Map<Construct.Component.Key, Construct.Component> components) {
            Construct.Component component = components.get(key);
            if (component == null) {
                throw new IllegalStateException("Component " + key + " not found");
            }

            MiningDeniedProperty property = component
                    .properties(role)
                    .get(type())
                    .orElseThrow(() -> new RuntimeException("Property " + type() + " not found in component " + key));

            return property.blocks(component.components());
        }
    }
}
