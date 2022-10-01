package se.gory_moon.chargers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.text.NumberFormat;
import java.util.List;

public class Utils {

    public static String clean(String in) {
        return in.replaceAll("\u00A0", " ");
    }

    public static String formatAndClean(int number)
    {
        return clean(NumberFormat.getInstance().format(number));
    }

    public static void addEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(energyStorage -> {
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFO.key(), formatAndClean(energyStorage.getEnergyStored()), formatAndClean(energyStorage.getMaxEnergyStored())));
        });
    }
}