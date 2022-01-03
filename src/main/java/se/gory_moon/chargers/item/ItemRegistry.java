package se.gory_moon.chargers.item;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Constants;

public class ItemRegistry {

    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();

    public static final ItemEntry<ChargerBlockItem> CHARGER_T1_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T1_BLOCK, Item.class));
    public static final ItemEntry<ChargerBlockItem> CHARGER_T2_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T2_BLOCK, Item.class));
    public static final ItemEntry<ChargerBlockItem> CHARGER_T3_ITEM = ItemEntry.cast(REGISTRATE.get(Constants.CHARGER_T3_BLOCK, Item.class));

    public static void init() {}

}
