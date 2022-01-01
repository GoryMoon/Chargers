package se.gory_moon.chargers.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import se.gory_moon.chargers.block.entity.CustomItemStackHandler;

import javax.annotation.Nonnull;

public class SlotInput extends SlotItemHandler {

    private final int index;

    public SlotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !((CustomItemStackHandler)getItemHandler()).extractItemInternal(index, 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack remove(int amount) {
        return ((CustomItemStackHandler)getItemHandler()).extractItemInternal(index, amount, false);
    }
}
