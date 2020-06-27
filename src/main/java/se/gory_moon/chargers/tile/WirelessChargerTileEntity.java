package se.gory_moon.chargers.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.PacketDistributor;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.blocks.WirelessChargerBlock;
import se.gory_moon.chargers.handler.WirelessHandler;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.concurrent.atomic.AtomicBoolean;

public class WirelessChargerTileEntity extends EnergyHolderTileEntity {

    private boolean registered = false;
    private int lastPowered = -1;
    private int availableEnergy;

    public WirelessChargerTileEntity(TileEntityType<WirelessChargerTileEntity> tileEntityType) {
        super(tileEntityType);
        setStorage(new CustomEnergyStorage(
                Configs.SERVER.wireless.storage.get(),
                Configs.SERVER.wireless.maxInput.get(),
                Configs.SERVER.wireless.maxOutput.get()));
    }

    @Override
    public void remove() {
        super.remove();
        WirelessHandler.INSTANCE.unRegister(this);
        registered = false;
    }

    @Override
    public void markDirty() {
        final BlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 2);
        super.markDirty();
    }

    @Override
    public void tick() {
        World world = getWorld();
        if (world == null)
            return;

        if (!world.isRemote && !registered) {
            WirelessHandler.INSTANCE.register(this);
            registered = true;
        }
        if (!world.isRemote) {
            Boolean powered = getBlockState().get(WirelessChargerBlock.POWERED);
            if (canCharge() != powered) {
                getWorld().setBlockState(getPos(), getBlockState().with(WirelessChargerBlock.POWERED, canCharge()));
            }
        }

        super.tick();
        if (lastPowered == -1 || (lastPowered == 0 && getStorage().getEnergyStored() > 0) || (lastPowered > 0 && getStorage().getEnergyStored() == 0)) {
           if (!world.isRemote) {
               PacketDistributor.TRACKING_CHUNK.with(() -> getWorld().getChunkAt(getPos())).send(getUpdatePacket());
           }
            lastPowered = getStorage().getEnergyStored();
            markDirty();
        }
    }

    public void updateAvailable() {
        availableEnergy = Math.min(getStorage().getMaxOutput(), getStorage().getEnergyStored());
    }

    public boolean chargeItems(NonNullList<ItemStack> items) {
        AtomicBoolean charged = new AtomicBoolean(false);
        for (int i = 0; i < items.size() && availableEnergy > 0; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
                    if (stack.getCount() == 1) {
                        int transferred = energyStorage.receiveEnergy(availableEnergy, false);
                        if (transferred > 0) {
                            getStorage().extractEnergy(transferred, false);
                            availableEnergy -= transferred;
                            charged.set(true);
                        }
                    }
                });
            }
        }
        return charged.get();
    }

    public boolean canCharge() {
        return getStorage().getEnergyStored() > 0 && !isPowered();
    }

    public boolean isPowered() {
        return world.isBlockLoaded(getPos()) && world.getRedstonePowerFromNeighbors(getPos()) > 0;
    }

    public int getAvailableEnergy() {
        return availableEnergy;
    }
}
