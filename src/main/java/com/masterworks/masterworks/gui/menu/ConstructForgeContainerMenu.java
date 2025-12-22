package com.masterworks.masterworks.gui.menu;

import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.MasterworksMenuTypes;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.transfer.item.ConstructForgeHandler;
import com.masterworks.masterworks.transfer.item.DefaultConstructForgeHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ConstructForgeContainerMenu extends AbstractContainerMenu {
    public ConstructForgeContainerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new DefaultConstructForgeHandler());
    }

    public ConstructForgeContainerMenu(int containerId, Inventory playerInventory,
            DefaultConstructForgeHandler handler) {
        super(MasterworksMenuTypes.CONSTRUCT_FORGE.get(), containerId);

        int centerX = 45, centerY = 45, radiusX = 60, radiusY = 45;

        addSlot(new TemplateSlot(handler, centerX, centerY));
        addSlot(new ResultSlot(handler, 160, centerY));

        for (int index = 0; index < DefaultConstructForgeHandler.COMPONENTS_COUNT; index++) {
            double angle = 2 * Math.PI * index / DefaultConstructForgeHandler.COMPONENTS_COUNT;
            int x = (int) (centerX + radiusX * Math.cos(angle));
            int y = (int) (centerY + radiusY * Math.sin(angle));

            addSlot(new ComponentSlot(handler, index, x, y));
        }

        addStandardInventorySlots(playerInventory, 8, 120);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    public static class ConstructForgeSlot extends ResourceHandlerSlot {
        protected final DefaultConstructForgeHandler handler;

        public ConstructForgeSlot(DefaultConstructForgeHandler handler, int index, int xPosition,
                int yPosition) {
            super(handler, handler::set, index, xPosition, yPosition);
            this.handler = handler;
        }
    }

    public static class TemplateSlot extends ConstructForgeSlot {
        public TemplateSlot(DefaultConstructForgeHandler handler, int xPosition, int yPosition) {
            super(handler, ConstructForgeHandler.TEMPLATE_INDEX, xPosition, yPosition);
        }
    }

    public static class ResultSlot extends ConstructForgeSlot {
        public ResultSlot(DefaultConstructForgeHandler handler, int xPosition, int yPosition) {
            super(handler, ConstructForgeHandler.RESULT_INDEX, xPosition, yPosition);
        }
    }

    public static class ComponentSlot extends ConstructForgeSlot {
        private final int componentIndex;

        public ComponentSlot(DefaultConstructForgeHandler handler, int componentIndex,
                int xPosition, int yPosition) {
            super(handler, ConstructForgeHandler.COMPONENTS_INDEX + componentIndex, xPosition,
                    yPosition);
            this.componentIndex = componentIndex;
        }

        public Stream<RoleReferenceLocation> getRoles() {
            return handler.getComponentRoles(componentIndex);
        }
    }
}
