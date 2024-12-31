package se.gory_moon.chargers.handler;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.item.ChargerBlockItem;
import se.gory_moon.chargers.item.ItemRegistry;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

public class CapabilityRegistrationHandler {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        // Blocks
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                BlockEntityRegistry.CHARGER_BE.get(),
                (entity, side) -> entity.getStorage()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntityRegistry.CHARGER_BE.get(),
                (entity, context) -> entity.getInventoryHandler()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                BlockEntityRegistry.WIRELESS_CHARGER_BE.get(),
                (entity, side) -> entity.getStorage()
        );

        // TODO register compat capabilities


        // Items
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> {
                    ChargerBlock block = (ChargerBlock) ((ChargerBlockItem) stack.getItem()).getBlock();
                    ChargerBlock.Tier tier = block.getTier();
                    return new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut(), tier.isCreative());
                },
                ItemRegistry.CHARGER_T1_ITEM,
                ItemRegistry.CHARGER_T2_ITEM,
                ItemRegistry.CHARGER_T3_ITEM,
                ItemRegistry.CHARGER_T4_ITEM,
                ItemRegistry.CHARGER_CREATIVE_ITEM
        );

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new CustomItemEnergyStorage(stack,
                        Configs.SERVER.wireless.storage.get(),
                        Configs.SERVER.wireless.maxInput.get(),
                        Configs.SERVER.wireless.maxOutput.get()),
                ItemRegistry.CHARGER_WIRELESS_ITEM
        );
    }
}
