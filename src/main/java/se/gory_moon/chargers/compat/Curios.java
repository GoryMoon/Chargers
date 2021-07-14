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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

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
            LazyOptional<ICuriosItemHandler> lazyOptional = CuriosApi.getCuriosHelper().getCuriosHandler(player);
            lazyOptional.ifPresent(handler -> {
                NonNullList<ItemStack> chargeList = NonNullList.create();
                chargeList.add(ItemStack.EMPTY);

                for (ICurioStacksHandler itemHandler : handler.getCurios().values()) {
                    if (charger.getAvailableEnergy() <= 0) break; // Early exit if we are out of energy this tick

                    IDynamicStackHandler stacks = itemHandler.getStacks();
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (charger.getAvailableEnergy() <= 0) break; // Early exit if we are out of energy this tick

                        ItemStack stack = stacks.extractItem(i, 2, true); // Test extract to make sure only one item
                        if (!stack.isEmpty() && stack.getCount() == 1) {
                            stack = stacks.extractItem(i, 1, false);
                            chargeList.set(0, stack);

                            if (charger.chargeItems(chargeList)) {
                                result.set(true); // Successful charge of an item
                            }
                            stacks.insertItem(i, stack, false);
                        }
                    }
                }
            });
        }
        return result.get();
    }

}
