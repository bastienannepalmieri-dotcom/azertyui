package com.bastien.tradingblock.client;

import com.bastien.tradingblock.menu.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientModEvents {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientModEvents::clientSetup);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.TRADING_MENU.get(), TradingScreen::new));
    }
}
