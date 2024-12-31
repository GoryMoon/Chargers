package se.gory_moon.chargers.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.block.BlockRegistry;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final DeferredItem<ChargerBlockItem> CHARGER_T1_ITEM = ITEMS.register(
            Constants.CHARGER_T1_BLOCK,
            () -> new ChargerBlockItem(BlockRegistry.CHARGER_BLOCK_T1.get(), new Item.Properties()
                    .rarity(Rarity.COMMON)
                    .component(ChargerDataComponents.ENERGY, 0L)));

    public static final DeferredItem<ChargerBlockItem> CHARGER_T2_ITEM = ITEMS.register(
            Constants.CHARGER_T2_BLOCK,
            () -> new ChargerBlockItem(BlockRegistry.CHARGER_BLOCK_T2.get(), new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .component(ChargerDataComponents.ENERGY, 0L)));

    public static final DeferredItem<ChargerBlockItem> CHARGER_T3_ITEM = ITEMS.register(
            Constants.CHARGER_T3_BLOCK,
            () -> new ChargerBlockItem(BlockRegistry.CHARGER_BLOCK_T3.get(), new Item.Properties()
                    .rarity(Rarity.RARE)
                    .component(ChargerDataComponents.ENERGY, 0L)));

    public static final DeferredItem<ChargerBlockItem> CHARGER_T4_ITEM = ITEMS.register(
            Constants.CHARGER_T4_BLOCK,
            () -> new ChargerBlockItem(BlockRegistry.CHARGER_BLOCK_T4.get(), new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .component(ChargerDataComponents.ENERGY, 0L)));

    public static final DeferredItem<ChargerBlockItem> CHARGER_CREATIVE_ITEM = ITEMS.register(
            Constants.CHARGER_CREATIVE_BLOCK,
            () -> new ChargerBlockItem(BlockRegistry.CHARGER_BLOCK_CREATIVE.get(), new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .component(ChargerDataComponents.ENERGY, Long.MAX_VALUE)));

    public static final DeferredItem<WirelessChargerBlockItem> CHARGER_WIRELESS_ITEM = ITEMS.register(
            Constants.WIRELESS_CHARGER_BLOCK,
            () -> new WirelessChargerBlockItem(BlockRegistry.WIRELESS_CHARGER.get(), new Item.Properties()
                    .component(ChargerDataComponents.ENERGY, 0L)));

    private static final Supplier<CreativeModeTab> CHARGERS_TAB = CREATIVE_TABS.register(Constants.MOD_ID, () -> CreativeModeTab.builder()
            .title(Component.translatable(LangKeys.CREATIVE_TAB.key()))
            .icon(ItemRegistry.CHARGER_T1_ITEM::toStack)
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((parameters, output) -> {
                output.accept(CHARGER_T1_ITEM.get());
                output.accept(CHARGER_T2_ITEM.get());
                output.accept(CHARGER_T3_ITEM.get());
                output.accept(CHARGER_T4_ITEM.get());
                output.accept(CHARGER_CREATIVE_ITEM.get());
                output.accept(CHARGER_WIRELESS_ITEM.get());
            }).build());
}
