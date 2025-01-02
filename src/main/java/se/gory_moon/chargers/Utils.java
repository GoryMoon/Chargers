package se.gory_moon.chargers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.text.DecimalFormat;
import java.util.List;

public class Utils {

    public static String clean(String in) {
        return in.replaceAll("\u00A0", " ");
    }

    public static String formatAndClean(long number) {
        return clean(new DecimalFormat().format(number));
    }

    public static Component formatEnergy(long energy) {
        return Component.literal(formatAndClean(energy) + ChatFormatting.DARK_AQUA + " FE");
    }

    public static String formatEnergyPerTick(long energy) {
        return ChatFormatting.WHITE + formatAndClean(energy) + ChatFormatting.DARK_AQUA + " FE" + ChatFormatting.GOLD + "/" + ChatFormatting.DARK_AQUA + "t";
    }

    public static Component formatFilledCapacity(long amount, long capacity) {
        return Component.literal(ChatFormatting.WHITE + formatAndClean(amount) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + formatAndClean(capacity) + ChatFormatting.DARK_AQUA + " FE");
    }

    public static void addEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage != null) {
            long stored;
            long max;
            if (energyStorage instanceof CustomEnergyStorage storage) {
                stored = storage.getLongEnergyStored();
                max = storage.getLongMaxEnergyStored();
            } else {
                stored = energyStorage.getEnergyStored();
                max = energyStorage.getMaxEnergyStored();
            }
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFO.key(), formatFilledCapacity(stored, max)).withStyle(ChatFormatting.GOLD));
        }
    }
}