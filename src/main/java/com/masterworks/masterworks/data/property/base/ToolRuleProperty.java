package com.masterworks.masterworks.data.property.base;

import java.util.List;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

public interface ToolRuleProperty extends Property {
    Tool.Rule getToolRule(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends ToolRuleProperty> extends Property.Type<P> {
    }

    static void apply(Construct construct, ItemStack stack) {
        List<Tool.Rule> rules = ConstructPropertyHelpers
                .taggedProperties(MasterworksTags.TOOL_RULE_PROPERTY_TYPES, construct)
                .map(property -> property.getToolRule(construct)).toList();

        if (rules.isEmpty()) {
            return;
        }

        stack.set(DataComponents.TOOL, new Tool(rules, 1.0f, 1, true));

    }
}
