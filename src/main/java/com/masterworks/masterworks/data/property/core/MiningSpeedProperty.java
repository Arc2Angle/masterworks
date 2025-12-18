package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.property.provider.ToolRuleProviderProperty;
import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.ExpressionProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

public record MiningSpeedProperty(Expression expression)
        implements ExpressionProperty, ToolRuleProviderProperty {
    @Override
    @Nullable
    public Tool.Rule get(Construct construct) {
        Double value = evaluate(construct);
        if (value == null) {
            return null;
        }

        /*
         * Named<Block> incorrect =
         * BuiltInRegistries.BLOCK.get(BlockTags.INCORRECT_FOR_IRON_TOOL).orElseThrow();
         */

        Named<Block> blocks =
                BuiltInRegistries.BLOCK.get(BlockTags.MINEABLE_WITH_PICKAXE).orElseThrow();

        return Tool.Rule.minesAndDrops(blocks, value.floatValue());
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.MINING_SPEED.get();
    }

    public static class Type implements ExpressionProperty.Type<MiningSpeedProperty>,
            ToolRuleProviderProperty.Type<MiningSpeedProperty> {
        @Override
        public String name() {
            return "Mining Speed";
        }

        @Override
        public MiningSpeedProperty create(Expression expression) {
            return new MiningSpeedProperty(expression);
        }
    }
}
