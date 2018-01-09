package se.gory_moon.chargers.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

public class Baubles {

    public static Baubles INSTANCE = new Baubles();
    private static final boolean loaded;

    static {
        loaded = Loader.isModLoaded("baubles");
    }

    public boolean chargeItems(EntityPlayer player, TileEntityWirelessCharger charger) {
        boolean result = false;
        if (Configs.compat.baublesCompat && loaded) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        stack = stack.copy();
                        if (charger.chargeItems(NonNullList.from(ItemStack.EMPTY, stack))) {
                            handler.setStackInSlot(i, stack);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

}
