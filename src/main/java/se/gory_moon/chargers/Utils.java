package se.gory_moon.chargers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.text.NumberFormat;
import java.util.List;

public class Utils {

    public static String clean(String in) {
        return in.replaceAll("\u00A0", " ");
    }

    public static String formatAndClean(long number) {
        return clean(NumberFormat.getInstance().format(number));
    }

    public static void addEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(energyStorage -> {
            String stored;
            String max;
            if (energyStorage instanceof CustomEnergyStorage storage) {
                stored = formatAndClean(storage.getLongEnergyStored());
                max = formatAndClean(storage.getLongMaxEnergyStored());
            } else {
                stored = formatAndClean(energyStorage.getEnergyStored());
                max = formatAndClean(energyStorage.getMaxEnergyStored());
            }
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFO.key(), stored, max));
        });
    }
}