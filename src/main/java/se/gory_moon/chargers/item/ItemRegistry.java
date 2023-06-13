package se.gory_moon.chargers.item;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Constants;

public final class ItemRegistry {

    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();
    private static final RegistryEntry<CreativeModeTab> CHARGERS_TAB = REGISTRATE.defaultCreativeTab(Constants.MOD_ID,
                    b -> b.withTabsBefore(CreativeModeTabs.SPAWN_EGGS).icon(ItemRegistry.CHARGER_T1_ITEM::asStack))
            .register();

    public static final ItemEntry<ChargerBlockItem> CHARGER_T1_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T1_BLOCK, ForgeRegistries.Keys.ITEMS));
    public static final ItemEntry<ChargerBlockItem> CHARGER_T2_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T2_BLOCK, ForgeRegistries.Keys.ITEMS));
    public static final ItemEntry<ChargerBlockItem> CHARGER_T3_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T3_BLOCK, ForgeRegistries.Keys.ITEMS));
    public static final ItemEntry<ChargerBlockItem> CHARGER_T4_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T4_BLOCK, ForgeRegistries.Keys.ITEMS));
    public static final ItemEntry<ChargerBlockItem> CHARGER_WIRELESS_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.WIRELESS_CHARGER_BLOCK, ForgeRegistries.Keys.ITEMS));

    public ItemRegistry() {}

    public static void init() {
        // Manually add the items to control the order
        REGISTRATE.modifyCreativeModeTab(CHARGERS_TAB.getKey(), modifier -> {
            modifier.accept(CHARGER_T1_ITEM.get());
            modifier.accept(CHARGER_T2_ITEM.get());
            modifier.accept(CHARGER_T3_ITEM.get());
            modifier.accept(CHARGER_T4_ITEM.get());
            modifier.accept(CHARGER_WIRELESS_ITEM.get());
        });
    }

}
