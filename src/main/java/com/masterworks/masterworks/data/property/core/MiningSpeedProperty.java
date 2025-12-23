package com.masterworks.masterworks.data.property.core;

import java.util.Map;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Decoder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

public record MiningSpeedProperty(Expression expression,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements ExpressionProperty, ToolRuleProperty {

    @Override
    public Tool.Rule getToolRule(Construct construct) {
        /*
         * Named<Block> incorrect =
         * BuiltInRegistries.BLOCK.get(BlockTags.INCORRECT_FOR_IRON_TOOL).orElseThrow();
         */

        Named<Block> blocks =
                BuiltInRegistries.BLOCK.get(BlockTags.MINEABLE_WITH_PICKAXE).orElseThrow();

        return Tool.Rule.minesAndDrops(blocks, evaluate(construct.components()).floatValue());
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.MINING_SPEED.get();
    }

    public static class Type extends ExpressionProperty.Type<MiningSpeedProperty>
            implements ToolRuleProperty.Type<MiningSpeedProperty> {
        @Override
        public Decoder<MiningSpeedProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return decoder(MiningSpeedProperty::new, components);
        }
    }
}
