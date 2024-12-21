package se.gory_moon.chargers.compat.fn;

import it.unimi.dsi.fastutil.Pair;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.Map;

public class FluxNetworksCompat {
    public static FluxNetworksCompat INSTANCE = new FluxNetworksCompat();

    boolean loaded = false;

    private FluxNetworksCompat() {
        this.loaded = ModList.get().isLoaded("fluxnetworks");
    }

    /**
     * Returns if Flux Networks is loaded or not
     *
     * @return The mod is loaded or not
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Checks if Flux Networks is loaded and that the provided capability is an FNEnergy cap
     *
     * @param cap The cap to check
     * @param <T> The cap type
     * @return If the provided cap is a FNEnergy cap
     */
    public <T> boolean isOpCapability(@NotNull Capability<T> cap) {
        if (loaded) {
            return cap == FluxCapabilities.FN_ENERGY_STORAGE;
        }

        return false;
    }

    /**
     * Creates a wrapper around the provided storage, invalidates any previous lazy optionals if available.
     *
     * @param storage        The storage to wrap
     * @param compatWrappers The map holding the wrappers
     */
    public void createFNWrapper(CustomEnergyStorage storage, Map<Capability<?>, Pair<IEnergyStorage, LazyOptional<IEnergyStorage>>> compatWrappers) {
        if (compatWrappers.containsKey(FluxCapabilities.FN_ENERGY_STORAGE)) {
            compatWrappers.get(FluxCapabilities.FN_ENERGY_STORAGE).second().invalidate();
        }
        var fnWrapper = new FNStorageWrapper(storage);
        compatWrappers.put(FluxCapabilities.FN_ENERGY_STORAGE, Pair.of(fnWrapper, LazyOptional.of(() -> fnWrapper)));
    }
}
