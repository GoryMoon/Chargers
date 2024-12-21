package se.gory_moon.chargers.compat.bc;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.fml.ModList;
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
        if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.extractOP(extractAmount, false));
        }
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
        if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.receiveOP(receiveAmount, false));
        }
        return Optional.empty();
    }

    /**
     * Checks if the storage is full or not
     *
     * @param storage The storage to check
     * @return If the storage is full or not, or empty if the storage wasn't an IOPStorage
     */
    public Optional<Boolean> isStorageFull(IEnergyStorage storage) {
        if (storage instanceof IOPStorage opStorage) {
            return Optional.of(opStorage.getOPStored() >= opStorage.getMaxOPStored());
        }
        return Optional.empty();
    }

    /**
     * Checks if BrandonsCore is loaded and that the provided capability is an OP cap
     *
     * @param cap The cap to check
     * @param <T> The cap type
     * @return If the provided cap is a OP cap
     */
    public <T> boolean isOpCapability(@NotNull Capability<T> cap) {
        if (loaded) {
            return cap == CapabilityOP.OP;
        }

        return false;
    }

    /**
     * Creates a wrapper around the provided storage, invalidates any previous lazy optionals if available.
     *
     * @param storage        The storage to wrap
     * @param compatWrappers The map holding the wrappers
     */
    public void createOpWrapper(CustomEnergyStorage storage, Map<Capability<?>, Pair<IEnergyStorage, LazyOptional<IEnergyStorage>>> compatWrappers) {
        if (compatWrappers.containsKey(CapabilityOP.OP)) {
            compatWrappers.get(CapabilityOP.OP).second().invalidate();
        }
        var opWrapper = new OpStorageWrapper(storage);
        compatWrappers.put(CapabilityOP.OP, Pair.of(opWrapper, LazyOptional.of(() -> opWrapper)));
    }
}
