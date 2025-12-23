package com.masterworks.masterworks.data.property.core;

import java.util.Map;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

public record MiningSpeedProperty(TagKey<Block> blocks, Expression expression,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements ExpressionProperty, ToolRuleProperty {

    @Override
    public Tool.Rule getToolRule(Construct construct) {
        HolderSet.Named<Block> set = BuiltInRegistries.BLOCK.get(blocks).orElseThrow();
        return Tool.Rule.minesAndDrops(set, evaluate(construct.components()).floatValue());
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.MINING_SPEED.get();
    }

    public static class Type extends ExpressionProperty.Type<MiningSpeedProperty>
            implements ToolRuleProperty.Type<MiningSpeedProperty> {
        record Data(TagKey<Block> blockTag, Dynamic<?> value) {
            static final Codec<Data> CODEC = Codec.withAlternative(
                    RecordCodecBuilder.create(instance -> instance
                            .group(TagKey.codec(Registries.BLOCK).fieldOf("block_tag")
                                    .forGetter(Data::blockTag),
                                    Codec.PASSTHROUGH.fieldOf("value").forGetter(Data::value))
                            .apply(instance, Data::new)),
                    Codec.PASSTHROUGH, value -> new Data(BlockTags.AIR, value));
        }

        @Override
        public Decoder<MiningSpeedProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<>() {
                @Override
                public <T> DataResult<MiningSpeedProperty> decode(Dynamic<T> input) {
                    return Data.CODEC.parse(input)
                            .flatMap(data -> decodeExpression(data.value(), components)
                                    .map(expression -> new MiningSpeedProperty(data.blockTag(),
                                            expression, components)));
                }
            });
        }
    }
}
