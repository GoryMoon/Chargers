package se.gory_moon.chargers.compat.fn;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.power.CustomEnergyStorage;

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
     * Registers the FNEnergy cap to the different chargers
     *
     * @param event The event used to register capabilities
     */
    public void registerFNCapability(@NotNull RegisterCapabilitiesEvent event) {
        if (loaded) {
            // TODO
        }
    }

    /**
     * Creates a wrapper around the provided storage, invalidates any previous lazy optionals if available.
     *
     * @param storage        The storage to wrap
     * @return Returns a wrapped energy storage
     */
    private IEnergyStorage createFNWrapper(CustomEnergyStorage storage) {
        /*if (compatWrappers.containsKey(FluxCapabilities.FN_ENERGY_STORAGE)) {
            compatWrappers.get(FluxCapabilities.FN_ENERGY_STORAGE).second().invalidate();
        }
        var fnWrapper = new FNStorageWrapper(storage);
        compatWrappers.put(FluxCapabilities.FN_ENERGY_STORAGE, Pair.of(fnWrapper, LazyOptional.of(() -> fnWrapper)));*/
        return null;
    }
}
