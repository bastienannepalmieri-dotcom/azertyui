package com.bastien.tradingblock.menu;

import com.bastien.tradingblock.TradingBlockMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TradingBlockMod.MOD_ID);

    public static final RegistryObject<MenuType<TradingMenu>> TRADING_MENU =
            MENUS.register("trading_menu", () -> IForgeMenuType.create(TradingMenu::new));
}
