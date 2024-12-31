package se.gory_moon.chargers.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.inventory.ChargerMenu;

import java.util.function.Supplier;

public final class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BlOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<ChargerBlockEntity>> CHARGER_BE = BlOCK_ENTITIES.register(
            Constants.CHARGER_BLOCK,
            () -> BlockEntityType.Builder.of(
                    ChargerBlockEntity::new,
                    BlockRegistry.CHARGER_BLOCK_T1.get(),
                    BlockRegistry.CHARGER_BLOCK_T2.get(),
                    BlockRegistry.CHARGER_BLOCK_T3.get(),
                    BlockRegistry.CHARGER_BLOCK_T4.get(),
                    BlockRegistry.CHARGER_BLOCK_CREATIVE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<WirelessChargerBlockEntity>> WIRELESS_CHARGER_BE = BlOCK_ENTITIES.register(
            Constants.WIRELESS_CHARGER_BLOCK,
            () -> BlockEntityType.Builder.of(
                    WirelessChargerBlockEntity::new,
                    BlockRegistry.WIRELESS_CHARGER.get()
            ).build(null)
    );

    public static final Supplier<MenuType<ChargerMenu>> CHARGER_CONTAINER = MENU_TYPES.register(
            Constants.CHARGER_BLOCK,
            () -> new MenuType<>(ChargerMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
