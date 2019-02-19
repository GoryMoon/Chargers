package se.gory_moon.chargers.items;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.blocks.BlockCharger;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class ItemChargerBlock extends ItemMultiTexture {

    public ItemChargerBlock(Block first, Block second, Mapper mapper) {
        super(first, second, mapper);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getMetadata() < 0 || stack.getMetadata() > 4)
            return EnumRarity.COMMON;
        return EnumRarity.values()[stack.getMetadata()];
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        int meta = stack.getMetadata();
        if (stack.getMetadata() < 0 || stack.getMetadata() > 4) {
            meta = 0;
        }
        BlockCharger.Tier tier = BlockCharger.Tier.values()[meta];
        CustomItemEnergyStorage storage = new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut());
        return new ItemEnergyCapabilityProvider(storage);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(0, false);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addEnergyTooltip(stack, tooltip);
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static void addEnergyTooltip(ItemStack stack, List<String> tooltip) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);

            NumberFormat format = NumberFormat.getInstance();
            TextComponentTranslation text = new TextComponentTranslation("chat.chargers.stored.info", format.format(capability.getEnergyStored()), format.format(capability.getMaxEnergyStored()));

            tooltip.add(text.getFormattedText());
        }
    }

}
