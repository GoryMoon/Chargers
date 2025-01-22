package se.gory_moon.chargers.compat.fn;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.EnergyHolderBlockEntity;
import se.gory_moon.chargers.item.ItemRegistry;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

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
            // Separated to its own class to fix class loading with lambdas
            CapabilityRegister.registerFNCapability(event);
        }
    }

    private static final class CapabilityRegister {
        private static void registerFNCapability(@NotNull RegisterCapabilitiesEvent event) {
            event.registerBlockEntity(
                    FluxCapabilities.BLOCK,
                    BlockEntityRegistry.CHARGER_BE.get(),
                    (ICapabilityProvider<EnergyHolderBlockEntity, Direction, IFNEnergyStorage>) (entity, side) -> new FNStorageWrapper(entity.getStorage())
            );

            event.registerBlockEntity(
                    FluxCapabilities.BLOCK,
                    BlockEntityRegistry.WIRELESS_CHARGER_BE.get(),
                    (ICapabilityProvider<EnergyHolderBlockEntity, Direction, IFNEnergyStorage>) (entity, side) -> new FNStorageWrapper(entity.getStorage())
            );

            event.registerItem(
                    FluxCapabilities.ITEM,
                    (stack, context) -> {
                        ChargerBlock block = (ChargerBlock) ((BlockItem) stack.getItem()).getBlock();
                        ChargerBlock.Tier tier = block.getTier();
                        return new FNStorageWrapper(new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut(), tier.isCreative()));
                    },
                    ItemRegistry.CHARGER_T1_ITEM,
                    ItemRegistry.CHARGER_T2_ITEM,
                    ItemRegistry.CHARGER_T3_ITEM,
                    ItemRegistry.CHARGER_T4_ITEM,
                    ItemRegistry.CHARGER_CREATIVE_ITEM
            );

            event.registerItem(
                    FluxCapabilities.ITEM,
                    (stack, context) -> new FNStorageWrapper(new CustomItemEnergyStorage(stack,
                            Configs.SERVER.wireless.storage.get(),
                            Configs.SERVER.wireless.maxInput.get(),
                            Configs.SERVER.wireless.maxOutput.get())),
                    ItemRegistry.CHARGER_WIRELESS_ITEM
            );
        }
    }
}
