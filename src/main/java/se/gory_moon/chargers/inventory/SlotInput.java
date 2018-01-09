package se.gory_moon.chargers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import se.gory_moon.chargers.tile.CustomItemStackHandler;

import javax.annotation.Nonnull;

public class SlotInput extends SlotItemHandler {

    private final int index;

    public SlotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return !((CustomItemStackHandler)getItemHandler()).extractItemInternal(index, 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return ((CustomItemStackHandler)getItemHandler()).extractItemInternal(index, amount, false);
    }
}
