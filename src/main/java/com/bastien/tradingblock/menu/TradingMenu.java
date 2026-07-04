package com.bastien.tradingblock.menu;

import com.bastien.tradingblock.block.ModBlocks;
import com.bastien.tradingblock.block.entity.TradingBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class TradingMenu extends AbstractContainerMenu {

    public final TradingBlockEntity blockEntity;
    private final Level level;

    /** Constructeur côté client : lit la position du bloc envoyée par le serveur. */
    public TradingMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, (TradingBlockEntity) inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    /** Constructeur côté serveur. */
    public TradingMenu(int containerId, Inventory inv, TradingBlockEntity entity) {
        super(ModMenus.TRADING_MENU.get(), containerId);
        this.blockEntity = entity;
        this.level = inv.player.level;

        IItemHandler handler = entity.getItemHandler();

        // Slot où le joueur dépose ses émeraudes
        this.addSlot(new SlotItemHandler(handler, TradingBlockEntity.SLOT_EMERALD, 26, 90));

        // Slot de sortie (lecture seule pour le joueur, rempli automatiquement par l'achat)
        this.addSlot(new SlotItemHandler(handler, TradingBlockEntity.SLOT_OUTPUT, 134, 90) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 146 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 204));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(stackInSlot, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Seul le slot émeraude (index 0) accepte les dépôts depuis l'inventaire du joueur
                if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                ModBlocks.TRADING_BLOCK.get());
    }
}
