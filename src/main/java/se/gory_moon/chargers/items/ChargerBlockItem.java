package se.gory_moon.chargers.items;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.blocks.ChargerBlock.Tier;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

import static se.gory_moon.chargers.items.ItemRegistry.CHARGER_T1_ITEM;
import static se.gory_moon.chargers.items.ItemRegistry.CHARGER_T2_ITEM;

public class ChargerBlockItem extends BlockItem {

    public ChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        Tier tier = CHARGER_T1_ITEM.is(this) ? Tier.I: CHARGER_T2_ITEM.is(this) ? Tier.II: Tier.III;
        return new ItemEnergyCapabilityProvider(new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut()));
    }

    @Override
    public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(0, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addEnergyTooltip(stack, tooltip);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static void addEnergyTooltip(ItemStack stack, List<ITextComponent> tooltip) {
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> {
            tooltip.add(new TranslationTextComponent(LangKeys.CHAT_STORED_INFO.key(), Utils.formatAndClean(energyStorage.getEnergyStored()), Utils.formatAndClean(energyStorage.getMaxEnergyStored())));
        });
    }

}
