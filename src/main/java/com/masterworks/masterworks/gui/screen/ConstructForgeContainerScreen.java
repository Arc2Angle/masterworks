package com.masterworks.masterworks.gui.screen;

import java.util.List;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.gui.menu.ConstructForgeContainerMenu;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ConstructForgeContainerScreen
        extends AbstractContainerScreen<ConstructForgeContainerMenu> {

    private static final ResourceLocation GENERIC_CONTAINER_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private static final int TICKS_PER_SWITCH = 40;


    public ConstructForgeContainerScreen(ConstructForgeContainerMenu menu,
            Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        titleLabelX = 8;
        titleLabelY = 0;
        inventoryLabelX = 8;
        inventoryLabelY = 102;
    }

    private int currentContainerTick = 0;

    @Override
    protected void containerTick() {
        currentContainerTick++;
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(@Nonnull GuiGraphics graphics, int x, int y) {
        super.renderTooltip(graphics, x, y);

        // if (this.menu.getCarried().isEmpty()
        // && this.hoveredSlot instanceof ConstructForgeContainerMenu.ComponentSlot slot
        // && slot.key != null) {
        // String text = slot.key.value();

        // graphics.renderTooltip(this.font,
        // List.of(ClientTooltipComponent
        // .create(Component.literal(text).getVisualOrderText())),
        // x, y, DefaultTooltipPositioner.INSTANCE, null);
        // }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX,
            int mouseY) {
        for (Slot slot : menu.slots) {
            int x = (width - imageWidth) / 2 + slot.x;
            int y = (height - imageHeight) / 2 + slot.y;

            drawSlotGeneric(graphics, x, y);

            if (slot instanceof ConstructForgeContainerMenu.ComponentSlot componentSlot) {
                List<ShapeReferenceLocation> shapes = componentSlot.getRoles()
                        .flatMap(role -> role.registered().value().examples().stream()).toList();

                if (shapes.isEmpty()) {
                    continue;
                }

                ShapeReferenceLocation shape =
                        shapes.get((currentContainerTick / TICKS_PER_SWITCH) % shapes.size());

                drawSlotItem(graphics, shape.value(), x, y);
            }
        }
    }

    // Uses the first slot of player inventory from the generic container background
    private void drawSlotGeneric(@Nonnull GuiGraphics graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GENERIC_CONTAINER_BACKGROUND, x - 1, y - 1, 7,
                71, 18, 18, 256, 256);
    }

    // Draws an item's texture in the slot area
    private void drawSlotItem(@Nonnull GuiGraphics graphics, ResourceLocation resourceLocation,
            int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, resourceLocation, x, y, 0, 0, 16, 16, 16, 16);
    }
}
