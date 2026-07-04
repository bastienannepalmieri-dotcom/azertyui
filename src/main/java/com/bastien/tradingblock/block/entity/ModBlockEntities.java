package com.bastien.tradingblock.block.entity;

import com.bastien.tradingblock.TradingBlockMod;
import com.bastien.tradingblock.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TradingBlockMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<TradingBlockEntity>> TRADING_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("trading_block", () ->
                    BlockEntityType.Builder.of(TradingBlockEntity::new, ModBlocks.TRADING_BLOCK.get()).build(null));
}
