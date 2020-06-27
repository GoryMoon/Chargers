package se.gory_moon.chargers.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CustomItemEnergyStorage extends CustomEnergyStorage {

    private final ItemStack stack;

    public CustomItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
    }

    public static CompoundNBT getOrCreateTag(ItemStack stack) {
        if (!stack.isEmpty() && stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
        }
        return stack.getTag();
    }

    @Override
    public int getEnergyStored() {
        return getOrCreateTag(stack).getInt("Energy");
    }

    @Override
    protected void setEnergyInternal(int energy) {
        getOrCreateTag(stack).putInt("Energy", getEnergyStored() + energy);
    }
}
