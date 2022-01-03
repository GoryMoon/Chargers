package se.gory_moon.chargers;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import se.gory_moon.chargers.item.ItemRegistry;

public class ChargersTab extends CreativeModeTab {

    public static final CreativeModeTab TAB = new ChargersTab();

    private ChargersTab() {
        super("chargers");
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.CHARGER_T1_ITEM.get());
    }
}
