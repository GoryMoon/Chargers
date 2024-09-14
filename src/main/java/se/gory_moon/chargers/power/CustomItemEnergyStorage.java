package se.gory_moon.chargers.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class CustomItemEnergyStorage extends CustomEnergyStorage {

    private final ItemStack stack;

    public CustomItemEnergyStorage(ItemStack stack, long capacity, long maxReceive, long maxExtract) {
        this(stack, capacity, maxReceive, maxExtract, false);
    }

    public CustomItemEnergyStorage(ItemStack stack, long capacity, long maxReceive, long maxExtract, boolean creative) {
        super(capacity, maxReceive, maxExtract, creative);
        this.stack = stack;
    }

    @Override
    public int getEnergyStored() {
        return this.creative || getLongEnergyStored() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) getLongEnergyStored();
    }

    @Override
    public long getLongEnergyStored() {
        if (this.creative) return Long.MAX_VALUE;

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ENERGY_TAG, Tag.TAG_INT))
            return tag.getInt(ENERGY_TAG);
        else
            return tag.getLong(ENERGY_TAG);
    }

    @Override
    protected void setEnergyInternal(long energy) {
        stack.getOrCreateTag().putLong(ENERGY_TAG, getLongEnergyStored() + energy);
    }
}
