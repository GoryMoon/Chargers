package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.WirelessChargerBlock;
import se.gory_moon.chargers.compat.ChargeCompat;
import se.gory_moon.chargers.handler.WirelessHandler;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.concurrent.atomic.AtomicBoolean;

public class WirelessChargerBlockEntity extends EnergyHolderBlockEntity {

    private boolean registered = false;
    private long lastPowered = -1;
    private long availableEnergy;

    public WirelessChargerBlockEntity(BlockEntityType<WirelessChargerBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        setStorage(new CustomEnergyStorage(
                Configs.SERVER.wireless.storage.get(),
                Configs.SERVER.wireless.maxInput.get(),
                Configs.SERVER.wireless.maxOutput.get()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        WirelessHandler.INSTANCE.unregister(this, getLevel());
        registered = false;
    }

    @Override
    public void setChanged() {
        Level level = getLevel();
        if (level != null)
        {
            final BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 2);
        }
        super.setChanged();
    }

    @Override
    public void tickServer() {
        Level level = getLevel();
        if (level == null)
            return;

        if (!level.isClientSide && !registered) {
            WirelessHandler.INSTANCE.register(this, level);
            registered = true;
        }
        if (!level.isClientSide) {
            Boolean powered = getBlockState().getValue(WirelessChargerBlock.POWERED);
            if (canCharge() != powered) {
                getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(WirelessChargerBlock.POWERED, canCharge()));
            }
        }

        super.tickServer();
        if (getStorage() != null && (lastPowered == -1 || (lastPowered == 0 && getStorage().getLongEnergyStored() > 0) || (lastPowered > 0 && getStorage().getLongEnergyStored() == 0))) {
           if (!level.isClientSide) {
               PacketDistributor.TRACKING_CHUNK.with(() -> getLevel().getChunkAt(getBlockPos())).send(getUpdatePacket());
           }
            lastPowered = getStorage().getLongEnergyStored();
            setChanged();
        }
    }

    public void updateAvailable() {
        if (getStorage() != null)
            availableEnergy = Math.min(getStorage().getMaxOutput(), getStorage().getLongEnergyStored());
    }

    public boolean chargeItems(NonNullList<ItemStack> items) {
        AtomicBoolean charged = new AtomicBoolean(false);
        if (getStorage() != null) {
            for (int i = 0; i < items.size() && availableEnergy > 0; i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    ChargeCompat.INSTANCE.chargeItem(stack, getStorage(), availableEnergy, (transferred) -> {
                        availableEnergy -= transferred;
                        charged.set(true);
                    });
                }
            }
        }
        return charged.get();
    }

    public boolean canCharge() {
        return getStorage() != null && getStorage().getLongMaxEnergyStored() > 0 && !isPowered();
    }

    public boolean isPowered() {
        //noinspection deprecation
        return getLevel() != null && getLevel().isAreaLoaded(getBlockPos(), 1) && getLevel().getBestNeighborSignal(getBlockPos()) > 0;
    }

    public long getAvailableEnergy() {
        return availableEnergy;
    }
}
