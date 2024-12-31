package se.gory_moon.chargers.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.entity.WirelessChargerBlockEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
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
    @Nullable
    public static IItemHandler getCurios(Player player) {
        /*if (isLoaded()) {
            return CuriosAPI.getCuriosHandler(player);
        }*/
        return null;
    }

    public static SlotItemHandler getSlot(Player player, IItemHandler itemHandler, int slot, int x, int y) {
        /*if (isLoaded()) {
            return new SlotBauble(player, (IBaublesItemHandler) itemHandler, slot, x, y);
        }*/
        return new SlotItemHandler(itemHandler, slot, x, y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public boolean chargeItems(Player player, WirelessChargerBlockEntity charger) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (isLoaded()) {
            var lazyOptional = CuriosApi.getCuriosInventory(player);
            lazyOptional.ifPresent(handler -> {
                NonNullList<ItemStack> chargeList = NonNullList.create();
                chargeList.add(ItemStack.EMPTY);

                for (ICurioStacksHandler itemHandler : handler.getCurios().values()) {
                    if (charger.getAvailableEnergy() <= 0) break; // Early exit if we are out of energy this tick

                    IDynamicStackHandler stacks = itemHandler.getStacks();
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (charger.getAvailableEnergy() <= 0) break; // Early exit if we are out of energy this tick

                        ItemStack stack = stacks.getStackInSlot(i);
                        if (!stack.isEmpty() && stack.getCount() == 1) {
                            chargeList.set(0, stack = stack.copy());

                            if (charger.chargeItems(chargeList)) {
                                stacks.setStackInSlot(i, stack);
                                result.set(true); // Successful charge of an item
                            }
                        }
                    }
                }
            });
        }
        return result.get();
    }

}
