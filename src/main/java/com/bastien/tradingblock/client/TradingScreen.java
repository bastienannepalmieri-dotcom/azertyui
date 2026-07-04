package com.bastien.tradingblock.client;

import com.bastien.tradingblock.menu.TradingMenu;
import com.bastien.tradingblock.network.ModNetwork;
import com.bastien.tradingblock.network.PacketBuyItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface du bloc d'échange.
 * Affiche une grille paginée de TOUS les items du jeu (vanilla + mods),
 * avec une barre de recherche. Cliquer sur un item envoie une demande
 * d'achat au serveur (1 émeraude consommée -> 64 exemplaires reçus).
 */
public class TradingScreen extends AbstractContainerScreen<TradingMenu> {

    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 5;
    private static final int ITEMS_PER_PAGE = GRID_COLS * GRID_ROWS;
    private static final int CELL_SIZE = 18;
    private static final int GRID_X = 8;
    private static final int GRID_Y = 30;

    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private int page = 0;

    private EditBox searchBox;

    public TradingScreen(TradingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 228;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();

        // On lit le registre d'items UNE fois à l'ouverture : vanilla + tous les mods installés.
        this.allItems = ForgeRegistries.ITEMS.getValues().stream()
                .filter(i -> i != Items.AIR)
                .sorted((a, b) -> a.getDescription().getString().compareToIgnoreCase(b.getDescription().getString()))
                .collect(Collectors.toList());
        this.filteredItems = new ArrayList<>(allItems);

        int gx = leftPos + GRID_X;
        int gy = topPos + GRID_Y;

        this.searchBox = new EditBox(this.font, gx, gy - 14, 160, 12,
                Component.translatable("gui.tradingblock.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.searchBox);

        this.addRenderableWidget(new Button(gx, gy + GRID_ROWS * CELL_SIZE + 6, 40, 14,
                Component.literal("<"), b -> changePage(-1)));
        this.addRenderableWidget(new Button(gx + 120, gy + GRID_ROWS * CELL_SIZE + 6, 40, 14,
                Component.literal(">"), b -> changePage(1)));

        this.setInitialFocus(this.searchBox);
    }

    private void onSearchChanged(String text) {
        String search = text.toLowerCase();
        if (search.isEmpty()) {
            this.filteredItems = new ArrayList<>(allItems);
        } else {
            this.filteredItems = allItems.stream()
                    .filter(i -> i.getDescription().getString().toLowerCase().contains(search)
                            || ForgeRegistries.ITEMS.getKey(i).toString().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }
        this.page = 0;
    }

    private void changePage(int dir) {
        int maxPage = Math.max(0, (filteredItems.size() - 1) / ITEMS_PER_PAGE);
        this.page = Math.max(0, Math.min(maxPage, this.page + dir));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        // Fond dessiné en code (pas besoin de texture custom)
        this.fill(poseStack, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        this.fill(poseStack, leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFF8B8B8B);

        int gx = leftPos + GRID_X;
        int gy = topPos + GRID_Y;

        // Cases de la grille d'items
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int x = gx + col * CELL_SIZE;
            int y = gy + row * CELL_SIZE;
            this.fill(poseStack, x, y, x + 16, y + 16, 0xFF373737);
        }

        // Zone des slots émeraude / sortie (léger contraste)
        this.fill(poseStack, leftPos + 20, topPos + 84, leftPos + 44, topPos + 108, 0xFF8B8B8B);
        this.fill(poseStack, leftPos + 128, topPos + 84, leftPos + 152, topPos + 108, 0xFF8B8B8B);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, 8, 6, 0x404040);
        this.font.draw(poseStack, Component.translatable("gui.tradingblock.rate"), 52, 88, 0x404040);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        int gx = leftPos + GRID_X;
        int gy = topPos + GRID_Y;
        int start = page * ITEMS_PER_PAGE;

        ItemStack hovered = ItemStack.EMPTY;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= filteredItems.size()) break;

            Item item = filteredItems.get(idx);
            ItemStack stack = new ItemStack(item);

            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int x = gx + col * CELL_SIZE + 1;
            int y = gy + row * CELL_SIZE + 1;

            this.itemRenderer.renderAndDecorateItem(stack, x, y);
            this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y);

            if (mouseX >= x - 1 && mouseX < x + 17 && mouseY >= y - 1 && mouseY < y + 17) {
                hovered = stack;
            }
        }

        int totalPages = Math.max(1, (int) Math.ceil(filteredItems.size() / (double) ITEMS_PER_PAGE));
        String pageText = (page + 1) + "/" + totalPages;
        this.font.draw(poseStack, pageText, gx + 68, gy + GRID_ROWS * CELL_SIZE + 10, 0x404040);

        if (!hovered.isEmpty()) {
            this.renderTooltip(poseStack, hovered, mouseX, mouseY);
        }

        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int gx = leftPos + GRID_X;
        int gy = topPos + GRID_Y;
        int start = page * ITEMS_PER_PAGE;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= filteredItems.size()) break;

            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int x = gx + col * CELL_SIZE;
            int y = gy + row * CELL_SIZE;

            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                Item item = filteredItems.get(idx);
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id != null) {
                    ModNetwork.CHANNEL.sendToServer(
                            new PacketBuyItem(this.menu.blockEntity.getBlockPos(), id));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
