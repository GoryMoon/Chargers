package se.gory_moon.chargers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.text.DecimalFormat;
import java.util.List;

public class EnergyFormatting {

    public static Component FE = Component.literal(" FE").withStyle(ChatFormatting.DARK_AQUA);
    public static Component SLASH = Component.literal("/").withStyle(ChatFormatting.GOLD);
    public static Component FE_TICK = FE.copy().append(SLASH).append(Component.literal("t").withStyle(ChatFormatting.DARK_AQUA));
    public static Component POSITIVE = Component.literal("+").withStyle(ChatFormatting.GREEN);
    public static Component NEGATIVE = Component.literal("-").withStyle(ChatFormatting.RED);

    public static Component formatAndClean(long number) {
        return Component.literal(new DecimalFormat().format(number)).withStyle(ChatFormatting.WHITE);
    }

    public static Component formatEnergyPerTick(long energy) {
        return formatAndClean(Math.abs(energy)).copy().append(FE_TICK).withStyle(ChatFormatting.WHITE);
    }

    public static Component formatFilledCapacity(long amount, long capacity) {
        return Component.translatable(LangKeys.POWER_INFO.key(),
                formatAndClean(amount).copy().append(SLASH).append(formatAndClean(capacity)).append(FE)
        ).withStyle(ChatFormatting.GOLD);
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
            tooltip.add(formatFilledCapacity(stored, max));
        }
    }
}