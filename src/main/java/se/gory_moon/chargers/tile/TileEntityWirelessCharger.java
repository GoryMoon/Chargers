package se.gory_moon.chargers.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.handler.WirelessHandler;
import se.gory_moon.chargers.network.MessageUpdatePower;
import se.gory_moon.chargers.network.PacketHandler;

import javax.annotation.Nullable;

public class TileEntityWirelessCharger extends TileEntity implements ITickable {

    public CustomEnergyStorage storage;
    private boolean registered = false;
    private int lastPowered = -1;

    public TileEntityWirelessCharger() {
        storage = new CustomEnergyStorage(Configs.chargers.wireless.wirelessStorage, Configs.chargers.wireless.wirelessMaxInput, Configs.chargers.wireless.wirelessMaxOutput);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        WirelessHandler.INSTANCE.unRegister(this);
        registered = false;
    }


    @Override
    public void update() {
        if (world.isRemote) return;

        if (!registered) {
            WirelessHandler.INSTANCE.register(this);
            registered = false;
        }
        if (lastPowered == -1 || (lastPowered == 0 && storage.getEnergyStored() > 0) || (lastPowered > 0 && storage.getEnergyStored() == 0)) {
            PacketHandler.INSTANCE.sendToAllAround(new MessageUpdatePower(this), this);
            lastPowered = storage.getEnergyStored();
        }
    }

    public boolean chargeItems(NonNullList<ItemStack> items) {
        boolean charged = false;
        int availableEnergy = Math.min(storage.getMaxOutput(), storage.getEnergyStored());
        for (int i = 0; i < items.size() && availableEnergy > 0; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (energyStorage != null && stack.getCount() == 1) {
                    int transferred = energyStorage.receiveEnergy(availableEnergy, false);
                    if (transferred > 0) {
                        storage.extractEnergy(transferred, false);
                        availableEnergy -= transferred;
                        charged = true;
                    }
                }
            }
        }
        return charged;
    }

    public boolean canCharge() {
        return storage.getEnergyStored() > 0 && !isPowered();
    }

    public boolean isPowered() {
        return world.isBlockLoaded(getPos()) && world.isBlockIndirectlyGettingPowered(getPos()) > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storage.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return storage.writeToNBT(super.writeToNBT(compound));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) storage;
        else
            return super.getCapability(capability, facing);
    }
}
