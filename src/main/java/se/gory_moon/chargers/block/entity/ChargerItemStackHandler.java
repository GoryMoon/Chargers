package se.gory_moon.chargers.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ChargerItemStackHandler extends ItemStackHandler {

    private final static int SIZE = 3;
    public ChargerItemStackHandler() {
        super(NonNullList.withSize(SIZE, ItemStack.EMPTY));
    }

    @Override
    public void setSize(int size) {
        stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            LazyOptional<IEnergyStorage> capability = stack.getCapability(CapabilityEnergy.ENERGY);
            if (slot == 0) {
                return capability.map(energyStorage -> energyStorage.receiveEnergy(1, true)).orElse(0) > 0;
            } else if (slot == 2) {
                return capability.map(energyStorage -> energyStorage.extractEnergy(1, true)).orElse(0) > 0;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if ((slot == 0 || slot == 2) && isItemValid(slot, stack))
            return super.insertItem(slot, stack, simulate);
        else
            return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0 || slot == 2)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }
}
