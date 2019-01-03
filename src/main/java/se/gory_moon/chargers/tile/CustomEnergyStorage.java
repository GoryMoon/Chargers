package se.gory_moon.chargers.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    private AverageCalculator in = new AverageCalculator();
    private AverageCalculator out = new AverageCalculator();
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

    public float getAverageChange() {
        return averageIn - averageOut;
    }

    public void tick() {
        in.tick(energyIn);
        out.tick(energyOut);
        averageIn = in.getAverage();
        averageOut = out.getAverage();
        energyIn = 0;
        energyOut = 0;
    }

    public void setEnergy(int energy) {
        if (energy > 0)
            energyIn += energy;
        else
            energyOut -= energy;
        this.energy += energy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            setEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            setEnergy(-energyExtracted);
        return energyExtracted;
    }

    public void readFromNBT(NBTTagCompound compound){
        energy = compound.getInteger("Energy");
        averageIn = compound.getFloat("In");
        averageOut = compound.getFloat("Out");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound.setInteger("Energy", this.getEnergyStored());
        compound.setFloat("In", in.getAverage());
        compound.setFloat("Out", out.getAverage());
        return compound;
    }

    private static class AverageCalculator {

        private int lastTotal;
        private int tickCount;
        private float[] cache;
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
