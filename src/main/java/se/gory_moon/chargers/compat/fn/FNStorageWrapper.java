package se.gory_moon.chargers.compat.fn;
/*
import net.neoforged.neoforge.energy.IEnergyStorage;
import se.gory_moon.chargers.power.CustomEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public record FNStorageWrapper(CustomEnergyStorage storage) implements IFNEnergyStorage, IEnergyStorage {

    @Override
    public long receiveEnergyL(long l, boolean b) {
        return storage.receiveLongEnergy(l, b);
    }

    @Override
    public long extractEnergyL(long l, boolean b) {
        return storage.extractLongEnergy(l, b);
    }

    @Override
    public long getEnergyStoredL() {
        return storage.getLongEnergyStored();
    }

    @Override
    public long getMaxEnergyStoredL() {
        return storage.getLongMaxEnergyStored();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }
}
*/