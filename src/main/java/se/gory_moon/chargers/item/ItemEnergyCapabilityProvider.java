package se.gory_moon.chargers.item;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemEnergyCapabilityProvider implements ICapabilityProvider {

    private CustomItemEnergyStorage storage;
    private final LazyOptional<CustomItemEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);

    public ItemEnergyCapabilityProvider(CustomItemEnergyStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return lazyStorage.cast();
        }
        return LazyOptional.empty();
    }
}
