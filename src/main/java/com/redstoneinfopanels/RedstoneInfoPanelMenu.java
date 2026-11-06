package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RedstoneInfoPanelMenu extends AbstractContainerMenu {

    private final BlockPos panelPos;

    // Серверный конструктор
    public RedstoneInfoPanelMenu(
            int containerId,
            Inventory playerInventory,
            BlockPos panelPos) {

        super(
                RedstoneInfoPanels.REDSTONE_INFO_PANEL_MENU.get(),
                containerId
        );

        this.panelPos = panelPos;
    }

    // Клиентский конструктор
    public RedstoneInfoPanelMenu(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf extraData) {

        this(
                containerId,
                playerInventory,
                extraData.readBlockPos()
        );
    }

    public BlockPos getPanelPos() {
        return panelPos;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(
            Player player,
            int index) {

        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}