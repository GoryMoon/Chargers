package se.gory_moon.chargers.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CustomItemStackHandler extends ItemStackHandler {

    private final int size;
    public CustomItemStackHandler(int size) {
        super(NonNullList.withSize(size, ItemStack.EMPTY));
        this.size = size;
    }

    @Override
    public void setSize(int size) {
        stacks = NonNullList.withSize(this.size, ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            LazyOptional<IEnergyStorage> capability = stack.getCapability(CapabilityEnergy.ENERGY);
            return capability.map(energyStorage -> energyStorage.receiveEnergy(1, true)).orElse(0) > 0;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot == 0 && isItemValid(slot, stack))
            return super.insertItem(slot, stack, simulate);
        else
            return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }
}
