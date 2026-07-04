package com.bastien.tradingblock;

import com.bastien.tradingblock.block.ModBlocks;
import com.bastien.tradingblock.block.entity.ModBlockEntities;
import com.bastien.tradingblock.client.ClientModEvents;
import com.bastien.tradingblock.menu.ModMenus;
import com.bastien.tradingblock.network.ModNetwork;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Mod "Trading Block" pour Bastien.
 * Ajoute un bloc qui permet d'acheter 64 exemplaires de N'IMPORTE QUEL item
 * enregistré dans le jeu (vanilla + tous les mods du modpack) pour 1 émeraude.
 *
 * La liste des items n'est PAS codée en dur : elle est lue dynamiquement
 * depuis le registre Forge (ForgeRegistries.ITEMS), donc ça marche avec
 * n'importe quelle combinaison de mods installés.
 */
@Mod(TradingBlockMod.MOD_ID)
public class TradingBlockMod {

    public static final String MOD_ID = "tradingblock";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> TRADING_BLOCK_ITEM = ITEMS.register("trading_block",
            () -> new BlockItem(ModBlocks.TRADING_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public TradingBlockMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        ModNetwork.register();
        ClientModEvents.init(modEventBus);
    }
}
