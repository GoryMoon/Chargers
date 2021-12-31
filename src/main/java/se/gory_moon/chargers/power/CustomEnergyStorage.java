package se.gory_moon.chargers.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    private final AverageCalculator in = new AverageCalculator();
    private final AverageCalculator out = new AverageCalculator();
    private int energyIn;
    private int energyOut;
    private float averageIn;
    private float averageOut;

    public int getMaxInput() {
        return maxReceive;
    }

    public int getMaxOutput() {
        return maxExtract;
    }

    public void tick() {
        in.tick(energyIn);
        out.tick(energyOut);
        averageIn = in.getAverage();
        averageOut = out.getAverage();
        energyIn = 0;
        energyOut = 0;
    }

    private void setEnergy(int energy) {
        if (energy > 0)
            energyIn += energy;
        else
            energyOut -= energy;
        setEnergyInternal(energy);
    }

    protected void setEnergyInternal(int energy) {
        this.energy += energy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - getEnergyStored(), Math.min(getMaxInput(), maxReceive));
        if (!simulate)
            setEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxOutput(), maxExtract));
        if (!simulate)
            setEnergy(-energyExtracted);
        return energyExtracted;
    }

    public void readFromNBT(CompoundTag compound){
        energy = compound.getInt("Energy");
        this.averageIn = compound.getFloat("In");
        this.averageOut = compound.getFloat("Out");
    }

    public CompoundTag writeToNBT(CompoundTag compound){
        compound.putInt("Energy", this.getEnergyStored());
        compound.putFloat("In", in.getAverage());
        compound.putFloat("Out", out.getAverage());
        return compound;
    }

    public float getAverageIn() {
        return averageIn;
    }

    public float getAverageOut() {
        return averageOut;
    }

    private static class AverageCalculator {

        private int lastTotal;
        private int tickCount;
        private final float[] cache;
        private int writeIndex;
        private int writeSize;

        private AverageCalculator() {
            cache = new float[2];
        }

        public float getAverage() {
            int ticks = tickCount + writeSize * 20;
            if (ticks == 0)
                return 0;
            float total = lastTotal;
            for (int i = writeIndex, c = writeSize; c-- > 0;) {
                total += cache[i];
                if (++i == cache.length)
                    i = 0;
            }
            return total / ticks;
        }

        public void tick(long val) {
            lastTotal += val;
            tickCount++;
            if (tickCount == 20) {
                cache[writeIndex++] = lastTotal;
                if (writeIndex > writeSize) {
                    writeSize = writeIndex;
                }
                if (writeIndex == cache.length) {
                    writeIndex = 0;
                }
                lastTotal = 0;
                tickCount = 0;
            }
        }
    }
}
