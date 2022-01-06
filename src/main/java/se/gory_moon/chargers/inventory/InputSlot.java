package se.gory_moon.chargers.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import se.gory_moon.chargers.block.entity.ChargerItemStackHandler;

import javax.annotation.Nonnull;

public class InputSlot extends SlotItemHandler {

    public InputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !((ChargerItemStackHandler)getItemHandler()).extractItemInternal(getSlotIndex(), 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack remove(int amount) {
        return ((ChargerItemStackHandler)getItemHandler()).extractItemInternal(getSlotIndex(), amount, false);
    }
}
