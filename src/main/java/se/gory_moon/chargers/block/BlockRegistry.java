package se.gory_moon.chargers.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.gory_moon.chargers.Constants;

public final class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, Constants.MOD_ID);

    public static final DeferredBlock<ChargerBlock> CHARGER_BLOCK_T1 = BLOCKS.registerBlock(
            Constants.CHARGER_T1_BLOCK,
            properties -> new ChargerBlock(ChargerBlock.Tier.I, properties),
            Block.Properties.of()
                    .strength(5, 10)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<ChargerBlock> CHARGER_BLOCK_T2 = BLOCKS.registerBlock(
            Constants.CHARGER_T2_BLOCK,
            properties -> new ChargerBlock(ChargerBlock.Tier.II, properties),
            BlockBehaviour.Properties.of()
                    .strength(5, 10)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<ChargerBlock> CHARGER_BLOCK_T3 = BLOCKS.registerBlock(
            Constants.CHARGER_T3_BLOCK,
            properties -> new ChargerBlock(ChargerBlock.Tier.III, properties),
            Block.Properties.of()
                    .strength(10, 10)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<ChargerBlock> CHARGER_BLOCK_T4 = BLOCKS.registerBlock(
            Constants.CHARGER_T4_BLOCK,
            properties -> new ChargerBlock(ChargerBlock.Tier.IV, properties),
            BlockBehaviour.Properties.of()
                    .strength(30, 1200)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<ChargerBlock> CHARGER_BLOCK_CREATIVE = BLOCKS.registerBlock(
            Constants.CHARGER_CREATIVE_BLOCK,
            properties -> new ChargerBlock(ChargerBlock.Tier.C, properties),
            BlockBehaviour.Properties.of()
                    .strength(50, 1200)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<WirelessChargerBlock> WIRELESS_CHARGER = BLOCKS.registerBlock(
            Constants.WIRELESS_CHARGER_BLOCK,
            WirelessChargerBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(5, 10)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ChargerBlock>> CHARGER_CODEC = BLOCK_TYPES.register(
            Constants.CHARGER_BLOCK,
            () -> RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ChargerBlock.Tier.CODEC.fieldOf("tier").forGetter(ChargerBlock::getTier),
                            BlockBehaviour.propertiesCodec()
                    ).apply(instance, ChargerBlock::new))
    );

    public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WirelessChargerBlock>> WIRELESS_CHARGER_CODEC = BLOCK_TYPES.register(
            Constants.WIRELESS_CHARGER_BLOCK,
            () -> BlockBehaviour.simpleCodec(WirelessChargerBlock::new)
    );
}
