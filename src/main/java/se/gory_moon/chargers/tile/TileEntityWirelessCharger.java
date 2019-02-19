package se.gory_moon.chargers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.handler.WirelessHandler;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public class TileEntityWirelessCharger extends TileEntityEnergyHolder {

    private boolean registered = false;
    private int lastPowered = -1;
    private int availableEnergy;

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
    public void markDirty() {
        final IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 2);
        super.markDirty();
    }

    @Override
    public void update() {
        if (!world.isRemote && !registered) {
            WirelessHandler.INSTANCE.register(this);
            registered = true;
        }

        super.update();
        if (lastPowered == -1 || (lastPowered == 0 && storage.getEnergyStored() > 0) || (lastPowered > 0 && storage.getEnergyStored() == 0)) {
           if (world.isRemote) {
               PacketHandler.sendToAllAround(getUpdatePacket(), this);
           }
            lastPowered = storage.getEnergyStored();
            markDirty();
        }
    }

    public void updateAvailable() {
        availableEnergy = Math.min(storage.getMaxOutput(), storage.getEnergyStored());
    }

    public boolean chargeItems(NonNullList<ItemStack> items) {
        boolean charged = false;
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
        return world.isBlockLoaded(getPos()) && world.getRedstonePowerFromNeighbors(getPos()) > 0;
    }
}
