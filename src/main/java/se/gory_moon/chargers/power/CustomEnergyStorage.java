package se.gory_moon.chargers.power;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.math.BigInteger;

public class CustomEnergyStorage implements IEnergyStorage, INBTSerializable<Tag> {

    public static final String ENERGY_TAG = "Energy";
    public static final String IN_TAG = "In";
    public static final String OUT_TAG = "Out";

    private long energy;
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;
    protected final boolean creative;

    private final AverageCalculator in = new AverageCalculator();
    private final AverageCalculator out = new AverageCalculator();

    private BigInteger energyIn = BigInteger.ZERO;
    private BigInteger energyOut = BigInteger.ZERO;
    private long averageIn;
    private long averageOut;

    public CustomEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, false);
    }

    public CustomEnergyStorage(long capacity, long maxReceive, long maxExtract, boolean creative) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.creative = creative;
        this.energy = creative ? Long.MAX_VALUE : 0;
    }

    public void tick() {
        in.tick(energyIn);
        out.tick(energyOut);
        averageIn = in.getAverage();
        averageOut = out.getAverage();
        energyIn = BigInteger.ZERO;
        energyOut = BigInteger.ZERO;
    }

    public void setEnergy(long energy) {
        if (energy > 0)
            energyIn = energyIn.add(BigInteger.valueOf(energy));
        else
            energyOut = energyOut.subtract(BigInteger.valueOf(energy));

        if (!creative)
            setEnergyInternal(energy);
    }

    protected void setEnergyInternal(long energy) {
        this.energy += energy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long received = receiveLongEnergy(maxReceive, simulate);
        return received > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long extracted = extractLongEnergy(maxExtract, simulate);
        return extracted > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) extracted;
    }

    @Override
    public int getEnergyStored() {
        return energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
    }

    public long receiveLongEnergy(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(capacity - getLongEnergyStored(), Math.min(getMaxInput(), maxReceive));
        if (!simulate)
            setEnergy(energyReceived);
        return creative ? maxReceive : energyReceived;
    }

    public long extractLongEnergy(long maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.min(getLongEnergyStored(), Math.min(getMaxOutput(), maxExtract));
        if (!simulate)
            setEnergy(-energyExtracted);
        return energyExtracted;
    }

    public long getLongEnergyStored() {
        return energy;
    }

    public long getLongMaxEnergyStored() {
        return creative ? Long.MAX_VALUE : capacity;
    }

    @Override
    public boolean canExtract() {
        return creative || maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return creative || maxReceive > 0;
    }

    public long getMaxInput() {
        return creative ? Long.MAX_VALUE : maxReceive;
    }

    public long getMaxOutput() {
        return creative ? Long.MAX_VALUE : maxExtract;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull Tag nbt) {
        if (nbt instanceof CompoundTag compound) {
            energy = compound.getLong(ENERGY_TAG);
            averageIn = compound.getLong(IN_TAG);
            averageOut = compound.getLong(OUT_TAG);
        }
    }

    @Override
    public @UnknownNullability Tag serializeNBT(@NotNull HolderLookup.Provider registries) {
        CompoundTag compound = new CompoundTag();
        compound.putLong(ENERGY_TAG, this.getLongEnergyStored());
        compound.putLong(IN_TAG, in.getAverage());
        compound.putLong(OUT_TAG, out.getAverage());
        return compound;
    }

    public long getAverageIn() {
        return averageIn;
    }

    public long getAverageOut() {
        return averageOut;
    }

    public boolean isCreative() {
        return creative;
    }

    private static class AverageCalculator {

        private BigInteger lastTotal = BigInteger.ZERO;
        private int tickCount;
        private final BigInteger[] cache;
        private int writeIndex;
        private int writeSize;

        private AverageCalculator() {
            cache = new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
        }

        public long getAverage() {
            int ticks = tickCount + writeSize * 20;
            if (ticks == 0)
                return 0;
            BigInteger total = lastTotal;
            for (int i = writeIndex, c = writeSize; c-- > 0; ) {
                total = total.add(cache[i]);
                if (++i == cache.length)
                    i = 0;
            }
            return total.divide(BigInteger.valueOf(ticks)).longValue();
        }

        public void tick(BigInteger val) {
            lastTotal = lastTotal.add(val);
            tickCount++;
            if (tickCount == 20) {
                cache[writeIndex++] = lastTotal;
                if (writeIndex > writeSize) {
                    writeSize = writeIndex;
                }
                if (writeIndex == cache.length) {
                    writeIndex = 0;
                }
                lastTotal = BigInteger.ZERO;
                tickCount = 0;
            }
        }
    }
}
