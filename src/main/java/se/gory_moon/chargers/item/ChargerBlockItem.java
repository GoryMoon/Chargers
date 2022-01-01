package se.gory_moon.chargers.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.block.ChargerBlock.Tier;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

import static se.gory_moon.chargers.item.ItemRegistry.CHARGER_T1_ITEM;
import static se.gory_moon.chargers.item.ItemRegistry.CHARGER_T2_ITEM;

public class ChargerBlockItem extends BlockItem {

    public ChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        Tier tier = CHARGER_T1_ITEM.is(this) ? Tier.I: CHARGER_T2_ITEM.is(this) ? Tier.II: Tier.III;
        return new ItemEnergyCapabilityProvider(new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut()));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(0, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        addEnergyTooltip(stack, tooltip);
        super.appendHoverText(stack, level, tooltip, flagIn);
    }

    public static void addEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> {
            tooltip.add(new TranslatableComponent(LangKeys.CHAT_STORED_INFO.key(), Utils.formatAndClean(energyStorage.getEnergyStored()), Utils.formatAndClean(energyStorage.getMaxEnergyStored())));
        });
    }

}
