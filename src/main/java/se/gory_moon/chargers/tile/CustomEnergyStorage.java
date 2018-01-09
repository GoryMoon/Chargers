package se.gory_moon.chargers.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int getMaxInput() {
        return maxReceive;
    }

    public int getMaxOutput() {
        return maxExtract;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void readFromNBT(NBTTagCompound compound){
        energy = compound.getInteger("Energy");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound.setInteger("Energy", this.getEnergyStored());
        return compound;
    }
}
