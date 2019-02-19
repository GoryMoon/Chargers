package se.gory_moon.chargers.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CustomItemEnergyStorage extends CustomEnergyStorage {

    private ItemStack stack;

    public CustomItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
    }

    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    @Override
    public int getEnergyStored() {
        return getOrCreateTag(stack).getInteger("Energy");
    }

    @Override
    protected void setEnergyInternal(int energy) {
        getOrCreateTag(stack).setInteger("Energy", getEnergyStored() + energy);
    }
}
