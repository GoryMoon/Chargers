package se.gory_moon.chargers.power;

import net.minecraft.world.item.ItemStack;
import se.gory_moon.chargers.item.ChargerDataComponents;

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

        return stack.getOrDefault(ChargerDataComponents.ENERGY, 0L);
    }

    @Override
    protected void setEnergyInternal(long energy) {
        stack.update(
                ChargerDataComponents.ENERGY,
                0L,
                e -> e + energy);
    }
}
