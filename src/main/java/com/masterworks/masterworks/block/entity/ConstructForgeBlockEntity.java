package com.masterworks.masterworks.block.entity;

import com.masterworks.masterworks.MasterworksBlockEntityTypes;
import com.masterworks.masterworks.gui.menu.ConstructForgeContainerMenu;
import com.masterworks.masterworks.transfer.item.DefaultConstructForgeHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class ConstructForgeBlockEntity extends BlockEntity implements MenuProvider {
    public final DefaultConstructForgeHandler inventory = new DefaultConstructForgeHandler() {
        @Override
        protected void onContentsChanged(int index, @Nonnull ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            setChanged();
        }
    };

    public ConstructForgeBlockEntity(BlockPos pos, BlockState state) {
        super(MasterworksBlockEntityTypes.CONSTRUCT_FORGE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input);
    }

    public ItemStacksResourceHandler inventoryCapability(Direction context) {
        return inventory;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(
            int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new ConstructForgeContainerMenu(containerId, playerInventory, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.masterworks.construct_forge");
    }
}
