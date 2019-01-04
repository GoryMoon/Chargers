package se.gory_moon.chargers.compat;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

public class Baubles {

    public static Baubles INSTANCE = new Baubles();
    private static final boolean loaded;

    static {
        loaded = Loader.isModLoaded("baubles");
    }

    public static boolean isLoaded() {
        return loaded && Configs.compat.baublesCompat;
    }

    public static IItemHandler getBaubles(EntityPlayer player) {
        if (isLoaded()) {
            return BaublesApi.getBaublesHandler(player);
        }
         return null;
    }

    public static SlotItemHandler getSlot(EntityPlayer player, IItemHandler itemHandler, int slot, int x, int y) {
        if (isLoaded()) {
            return new SlotBauble(player, (IBaublesItemHandler) itemHandler, slot, x, y);
        }
        return new SlotItemHandler(itemHandler, slot, x, y) {
            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        };
    }

    public boolean chargeItems(EntityPlayer player, TileEntityWirelessCharger charger) {
        boolean result = false;
        if (isLoaded()) {
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
