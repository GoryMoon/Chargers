package se.gory_moon.chargers.items;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemEnergyCapabilityProvider implements ICapabilityProvider {

    private final CustomItemEnergyStorage storage;

    public ItemEnergyCapabilityProvider(CustomItemEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(storage);
        } else {
            return null;
        }
    }
}
