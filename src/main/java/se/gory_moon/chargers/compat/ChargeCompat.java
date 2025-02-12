package se.gory_moon.chargers.compat;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import se.gory_moon.chargers.compat.bc.BrandonsCoreCompat;
import se.gory_moon.chargers.compat.fn.FluxNetworksCompat;
import se.gory_moon.chargers.compat.industrial.IndustrialForegoingCompat;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ChargeCompat {

    public static ChargeCompat INSTANCE = new ChargeCompat();

    private ChargeCompat() {}

    /**
     * A helper function to cap the long value to {@link Integer#MAX_VALUE}
     * @param value The value to cap
     * @return The input value as an integer or {@link Integer#MAX_VALUE}
     */
    private int cap(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    /**
     * Registers capabilities for compatibility with other mods
     * @param event The passed event used to register
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (BrandonsCoreCompat.INSTANCE.isLoaded()) {
            BrandonsCoreCompat.INSTANCE.registerOPCapability(event);
        }

        if (FluxNetworksCompat.INSTANCE.isLoaded()) {
            FluxNetworksCompat.INSTANCE.registerFNCapability(event);
        }
    }

    /**
     * Discharge an item into the block storage
     *
     * @param stack The item to discharge
     * @param blockStorage The storage to add the energy to
     * @param callback A callback that is called when the
     */
    public void dischargeItem(ItemStack stack, CustomEnergyStorage blockStorage, Runnable callback) {
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage != null) {
            long extractAmount = Math.min(blockStorage.getLongMaxEnergyStored() - blockStorage.getLongEnergyStored(), blockStorage.getMaxInput());

            long transferred = extractAmount(storage, extractAmount);
            if (transferred > 0) {
                blockStorage.receiveLongEnergy(transferred, false);
                callback.run();
            }
        }
    }

    /**
     * Handles charging of an item
     * The storage isn't extracted from automatically, needs to be done in the callback
     *
     * @param stack The item to charge
     * @param blockStorage The storage to use energy from
     * @param transferredCallback A callback that is called when an item received energy, extract the returned amount from storage here
     * @return Returns if the item being charged is full
     */
    public boolean chargeItem(ItemStack stack, CustomEnergyStorage blockStorage, Consumer<Long> transferredCallback) {
        return chargeItem(stack, blockStorage, -1, transferredCallback);
    }

    /**
     * Handles charging of an item with a specified transfer amount
     * The storage isn't extracted from automatically, needs to be done in the callback
     *
     * @param stack The item to charge
     * @param blockStorage The storage to use energy from
     * @param overrideTransfer If larger than -1 this is used as the retrieve amount
     * @param transferredCallback A callback that is called when an item received energy, extract the returned amount from storage here
     * @return Returns if the item being charged is full
     */
    public boolean chargeItem(ItemStack stack, CustomEnergyStorage blockStorage, long overrideTransfer, Consumer<Long> transferredCallback) {
        AtomicBoolean charged = new AtomicBoolean(false);

        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage != null) {
            var transferRequest = overrideTransfer;
            if (transferRequest < 0)
                transferRequest = blockStorage.extractLongEnergy(blockStorage.getLongMaxEnergyStored(), true);

            var transferred = receiveAmount(storage, transferRequest);
            if (transferred > 0) {
                transferredCallback.accept(transferred);
            }

            charged.set(isStorageFull(storage));
        }

        return charged.get();
    }

    /**
     * Helper function for special handling of extracting power from an item
     * @param storage The storage of the item
     * @param extractAmount The requested amount to extract form the item
     * @return The amount that was extracted
     */
    private long extractAmount(IEnergyStorage storage, long extractAmount) {
        if (storage instanceof CustomEnergyStorage customEnergyStorage) {
            return customEnergyStorage.extractLongEnergy(extractAmount, false);
        }

        if (BrandonsCoreCompat.INSTANCE.isLoaded()) {
            var amount = BrandonsCoreCompat.INSTANCE.extractAmount(storage, extractAmount);
            if (amount.isPresent()) {
                return amount.get();
            }
        }

        return storage.extractEnergy(cap(extractAmount), false);
    }

    /**
     * Helper function for special handling of inserting power into a block
     * @param storage The storage of the item
     * @param receiveAmount The max amount to insert into the item
     * @return The amount that was inserted
     */
    private long receiveAmount(IEnergyStorage storage, long receiveAmount) {
        if (storage instanceof CustomEnergyStorage customEnergyStorage) {
            return customEnergyStorage.receiveLongEnergy(receiveAmount, false);
        }

        if (BrandonsCoreCompat.INSTANCE.isLoaded()) {
            var amount = BrandonsCoreCompat.INSTANCE.receiveAmount(storage, receiveAmount);
            if (amount.isPresent()) {
                return amount.get();
            }
        }

        if (IndustrialForegoingCompat.INSTANCE.isLoaded()) {
            var amount = IndustrialForegoingCompat.INSTANCE.receiveAmount(storage, receiveAmount);
            if (amount.isPresent()) {
                return amount.get();
            }
        }

        return storage.receiveEnergy(cap(receiveAmount), false);
    }

    /**
     * Helper function for special handling to check if a storage is fully charged
     * @param storage The storage of the items to check
     * @return If the storage is full
     */
    private boolean isStorageFull(IEnergyStorage storage) {
        if (storage instanceof CustomEnergyStorage customEnergyStorage) {
            return customEnergyStorage.getLongEnergyStored() >= customEnergyStorage.getLongMaxEnergyStored();
        }

        if (BrandonsCoreCompat.INSTANCE.isLoaded()) {
            var isFull = BrandonsCoreCompat.INSTANCE.isStorageFull(storage);
            if (isFull.isPresent()) {
                return isFull.get();
            }
        }

        if (IndustrialForegoingCompat.INSTANCE.isLoaded()) {
            var isFull = IndustrialForegoingCompat.INSTANCE.isStorageFull(storage);
            if (isFull.isPresent()) {
                return isFull.get();
            }
        }

        return storage.getEnergyStored() >= storage.getMaxEnergyStored();
    }
}
