package se.gory_moon.chargers.compat.industrial;

import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class IndustrialForegoingCompat {
    public static IndustrialForegoingCompat INSTANCE = new IndustrialForegoingCompat();

    private boolean isLoaded = false;

    private IndustrialForegoingCompat() {
        isLoaded = ModList.get().isLoaded("industrialforegoing");
    }

    /**
     * Returns if Industrial Foregoing is loaded or not
     *
     * @return The mod is loaded or not
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Tries to receive energy into an InfinityEnergyStorage
     *
     * @param storage       The storage to check
     * @param receiveAmount The max amount to insert
     * @return The amount that was successfully inserted, or empty if the storage wasn't an InfinityEnergyStorage
     */
    public Optional<Long> receiveAmount(IEnergyStorage storage, long receiveAmount) {
        if (storage instanceof InfinityEnergyStorage<?> ieStorage) {
            long toAdd = Math.min(Long.MAX_VALUE - ieStorage.getLongEnergyStored(), receiveAmount);

            // Set the new amount into the storage
            ieStorage.setEnergyStored(ieStorage.getLongEnergyStored() + toAdd);
            return Optional.of(toAdd);
        }
        return Optional.empty();
    }

    /**
     * Checks if the storage is full or not
     *
     * @param storage The storage to check
     * @return If the storage is full or not, or empty if the storage wasn't an InfinityEnergyStorage
     */
    public Optional<Boolean> isStorageFull(IEnergyStorage storage) {
        if (storage instanceof InfinityEnergyStorage<?> ieStorage) {
            return Optional.of(ieStorage.getLongEnergyStored() >= ieStorage.getLongCapacity());
        }
        return Optional.empty();
    }
}
