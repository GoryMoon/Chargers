package se.gory_moon.chargers.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.tile.WirelessChargerTileEntity;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class Curios {

    public static Curios INSTANCE = new Curios();
    private static final boolean loaded;

    static {
        loaded = ModList.get().isLoaded("curios");
    }

    public static boolean isLoaded() {
        return loaded && Configs.COMMON.curiosCompat.get();
    }

    // TODO Curios inventory for gui
    public static IItemHandler getCurios(PlayerEntity player) {
        /*if (isLoaded()) {
            return CuriosAPI.getCuriosHandler(player);
        }*/
        return null;
    }

    public static SlotItemHandler getSlot(PlayerEntity player, IItemHandler itemHandler, int slot, int x, int y) {
        /*if (isLoaded()) {
            return new SlotBauble(player, (IBaublesItemHandler) itemHandler, slot, x, y);
        }*/
        return new SlotItemHandler(itemHandler, slot, x, y) {
            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        };
    }

    public boolean chargeItems(PlayerEntity player, WirelessChargerTileEntity charger) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (isLoaded()) {
            LazyOptional<ICurioItemHandler> lazyOptional = CuriosAPI.getCuriosHandler(player);
            lazyOptional.ifPresent(handler -> {
                for (CurioStackHandler itemHandler : handler.getCurioMap().values()) {
                    if (charger.getAvailableEnergy() <= 0) break;
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (charger.getAvailableEnergy() <= 0) break;
                        ItemStack stack = itemHandler.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            stack = stack.copy();
                            if (charger.chargeItems(NonNullList.from(ItemStack.EMPTY, stack))) {
                                itemHandler.setStackInSlot(i, stack);
                                result.set(true);
                            }
                        }
                    }
                }
            });
        }
        return result.get();
    }

}
