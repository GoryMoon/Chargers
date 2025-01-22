package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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

    public WirelessChargerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.WIRELESS_CHARGER_BE.get(), pos, state);
        setStorage(new CustomEnergyStorage(
                Configs.SERVER.wireless.storage.get(),
                Configs.SERVER.wireless.maxInput.get(),
                Configs.SERVER.wireless.maxOutput.get()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        registered = false;
    }

    @Override
    public void setChanged() {
        if (level != null) {
            final BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, WirelessChargerBlock.UPDATE_CLIENTS);
        }
        super.setChanged();
    }

    @Override
    public void tickServer() {
        if (level == null) return;

        if (!registered) {
            WirelessHandler.INSTANCE.register(this, level);
            registered = true;
        }

        var powered = getBlockState().getValue(WirelessChargerBlock.POWERED);
        if (canCharge() != powered) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(WirelessChargerBlock.POWERED, canCharge()));
        }
        super.tickServer();

        CustomEnergyStorage storage = getStorage();
        if (storage != null && (lastPowered == -1 || (lastPowered == 0 && storage.getLongEnergyStored() > 0) || (lastPowered > 0 && storage.getLongEnergyStored() == 0))) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), WirelessChargerBlock.UPDATE_CLIENTS);
            lastPowered = storage.getLongEnergyStored();
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
                        getStorage().extractLongEnergy(transferred, false);
                        availableEnergy -= transferred;
                        charged.set(true);
                    });
                }
            }
        }
        return charged.get();
    }

    public boolean canCharge() {
        return getStorage() != null && getStorage().getLongEnergyStored() > 0 && !isPowered();
    }

    public boolean isPowered() {
        return level != null && level.isAreaLoaded(getBlockPos(), 1) && level.getBestNeighborSignal(getBlockPos()) > 0;
    }

    public long getAvailableEnergy() {
        return availableEnergy;
    }
}
