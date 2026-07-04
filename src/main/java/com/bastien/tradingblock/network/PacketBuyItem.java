package com.bastien.tradingblock.network;

import com.bastien.tradingblock.block.entity.TradingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Envoyé du client vers le serveur quand le joueur clique sur un item dans la grille.
 * Le serveur vérifie qu'il y a bien une émeraude dans le slot, la consomme,
 * et donne 64 exemplaires de l'item demandé au joueur.
 */
public class PacketBuyItem {

    /** 1 émeraude = 64 exemplaires (taux d'échange fixe demandé). */
    private static final int QUANTITY_PER_EMERALD = 64;

    private final BlockPos pos;
    private final ResourceLocation itemId;

    public PacketBuyItem(BlockPos pos, ResourceLocation itemId) {
        this.pos = pos;
        this.itemId = itemId;
    }

    public static void encode(PacketBuyItem msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeResourceLocation(msg.itemId);
    }

    public static PacketBuyItem decode(FriendlyByteBuf buf) {
        return new PacketBuyItem(buf.readBlockPos(), buf.readResourceLocation());
    }

    public static void handle(PacketBuyItem msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level;
            if (!level.isLoaded(msg.pos)) return;

            // Sécurité : le joueur doit être raisonnablement proche du bloc
            if (player.blockPosition().distSqr(msg.pos) > 64 * 64) return;

            BlockEntity be = level.getBlockEntity(msg.pos);
            if (!(be instanceof TradingBlockEntity tradingBlockEntity)) return;

            Item item = ForgeRegistries.ITEMS.getValue(msg.itemId);
            if (item == null || item == Items.AIR) return;

            ItemStackHandler handler = tradingBlockEntity.getItemHandler();
            ItemStack emeraldSlot = handler.getStackInSlot(TradingBlockEntity.SLOT_EMERALD);

            if (emeraldSlot.isEmpty() || !emeraldSlot.is(Items.EMERALD)) {
                player.displayClientMessage(Component.translatable("message.tradingblock.no_emerald"), true);
                return;
            }

            ItemStack result = new ItemStack(item, QUANTITY_PER_EMERALD);

            // On essaie de donner directement dans l'inventaire du joueur,
            // sinon on fait apparaître l'item au sol pour ne rien perdre.
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }

            emeraldSlot.shrink(1);
            handler.setStackInSlot(TradingBlockEntity.SLOT_EMERALD, emeraldSlot);
            tradingBlockEntity.setChanged();
        });
        ctx.setPacketHandled(true);
    }
}
