package com.masterworks.masterworks.data.property.base;

import java.util.List;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

public interface ToolRuleProperty extends ConstructProperty<Tool.Rule> {
    @Override
    Type<?> type();

    interface Type<P extends ToolRuleProperty> extends ConstructProperty.Type<Tool.Rule, P> {
    }

    static void apply(Construct construct, ItemStack stack) {
        ConstructProperty.apply(MasterworksTags.TOOL_RULE_PROPERTY_TYPES, construct, values -> {
            List<Tool.Rule> rules = values.toList();
            if (rules.isEmpty()) {
                return;
            }

            stack.set(DataComponents.TOOL, new Tool(rules, 1.0f, 1, true));
        });
    }
}
