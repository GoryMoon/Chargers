package se.gory_moon.chargers.compat.bc;

/*
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public class OpStorageWrapper implements IOPStorage {
    private final CustomEnergyStorage storage;
    private final CustomIOInfo info;

    public OpStorageWrapper(CustomEnergyStorage storage) {
        this.storage = storage;
        this.info = new CustomIOInfo(storage);
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        return storage.receiveLongEnergy(maxReceive, simulate);
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        return storage.extractLongEnergy(maxExtract, simulate);
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
    public long getMaxOPStored() {
        return storage.getLongMaxEnergyStored();
    }

    @Override
    public long getOPStored() {
        return storage.getLongEnergyStored();
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

    @Override
    public long maxExtract() {
        return storage.getMaxOutput();
    }

    @Override
    public long maxReceive() {
        return storage.getMaxInput();
    }

    @Override
    public long modifyEnergyStored(long amount) {
        storage.setEnergy(amount);
        return Math.abs(amount);
    }

    @Override
    public @Nullable IOInfo getIOInfo() {
        return info;
    }

    public record CustomIOInfo(CustomEnergyStorage storage) implements IOInfo {
        @Override
        public long currentInput() {
            return storage.getAverageIn();
        }

        @Override
        public long currentOutput() {
            return storage.getAverageOut();
        }
    }
}
*/