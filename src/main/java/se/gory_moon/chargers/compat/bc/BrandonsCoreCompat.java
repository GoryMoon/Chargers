package se.gory_moon.chargers.compat.bc;

import it.unimi.dsi.fastutil.Pair;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.Map;
import java.util.Optional;

public class BrandonsCoreCompat {
    public static BrandonsCoreCompat INSTANCE = new BrandonsCoreCompat();
    private final boolean loaded;

    private BrandonsCoreCompat() {
        loaded = ModList.get().isLoaded("brandonscore");
    }

    /**
     * Returns if BrandonsCore is loaded or not
     *
     * @return The mod is loaded or not
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Tries to extract from an OP Storage
     *
     * @param storage       The storage to check
     * @param extractAmount The max amount to extract
     * @return The amount that was successfully extracted, or empty if the storage wasn't an IOPStorage
     */
    public Optional<Long> extractAmount(IEnergyStorage storage, long extractAmount) {
        /* if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.extractOP(extractAmount, false));
        }*/
        return Optional.empty();
    }

    /**
     * Tries to receive energy into an OP Storage
     *
     * @param storage       The storage to check
     * @param receiveAmount The max amount to insert
     * @return The amount that was successfully inserted, or empty if the storage wasn't an IOPStorage
     */
    public Optional<Long> receiveAmount(IEnergyStorage storage, long receiveAmount) {
        /*if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.receiveOP(receiveAmount, false));
        }*/
        return Optional.empty();
    }

    /**
     * Checks if the storage is full or not
     *
     * @param storage The storage to check
     * @return If the storage is full or not, or empty if the storage wasn't an IOPStorage
     */
    public Optional<Boolean> isStorageFull(IEnergyStorage storage) {
        /*if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.getOPStored() >= opStorage.getMaxOPStored());
        }*/
        return Optional.empty();
    }

    /**
     * Registers the OP cap to the different chargers
     *
     * @param event The event used to register capabilities
     */
    public void registerOPCapability(@NotNull RegisterCapabilitiesEvent event) {
        if (loaded) {
            // TODO
        }
    }

    /**
     * Creates a wrapper around the provided storage, invalidates any previous lazy optionals if available.
     *
     * @param storage        The storage to wrap
     * @param compatWrappers The map holding the wrappers
     */
    public void createOpWrapper(CustomEnergyStorage storage, Map<ItemCapability<IEnergyStorage, Void>, Pair<IEnergyStorage, IEnergyStorage>> compatWrappers) {
        /*if (compatWrappers.containsKey(CapabilityOP.OP)) {
            compatWrappers.get(CapabilityOP.OP).second().invalidate();
        }
        var opWrapper = new OpStorageWrapper(storage);
        compatWrappers.put(CapabilityOP.OP, Pair.of(opWrapper, LazyOptional.of(() -> opWrapper)));*/
    }
}
