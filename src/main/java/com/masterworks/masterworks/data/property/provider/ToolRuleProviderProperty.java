package com.masterworks.masterworks.data.property.provider;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

public interface ToolRuleProviderProperty extends Property {
    @Nullable
    Tool.Rule get(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends ToolRuleProviderProperty> extends Property.Type<P> {
    }

    class Builder {
        private final List<Tool.Rule> rules = new ArrayList<>();

        public void add(Type<?> type, Construct construct) {
            Tool.Rule rule = construct.getPropertyOrThrow(type, RoleReferenceResourceLocation.ITEM)
                    .get(construct);
            if (rule == null) {
                return;
            }

            rules.add(rule);
        }

        public void apply(ItemStack stack) {
            if (rules.isEmpty()) {
                return;
            }

            stack.set(DataComponents.TOOL, new Tool(rules, 1.0f, 1, true));
        }
    }
}
