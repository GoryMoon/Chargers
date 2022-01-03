package se.gory_moon.chargers.power;

import net.minecraft.world.item.ItemStack;

public class CustomItemEnergyStorage extends CustomEnergyStorage {

    private final ItemStack stack;

    public CustomItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
    }

    @Override
    public int getEnergyStored() {
        return stack.getOrCreateTag().getInt("Energy");
    }

    @Override
    protected void setEnergyInternal(int energy) {
        stack.getOrCreateTag().putInt("Energy", getEnergyStored() + energy);
    }
}
