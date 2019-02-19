package se.gory_moon.chargers.items;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWirelessChargerBlock extends ItemBlock {

    public ItemWirelessChargerBlock(Block block) {
        super(block);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        CustomItemEnergyStorage storage = new CustomItemEnergyStorage(stack, Configs.chargers.wireless.wirelessStorage, Configs.chargers.wireless.wirelessMaxInput, Configs.chargers.wireless.wirelessMaxOutput);
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
        ItemChargerBlock.addEnergyTooltip(stack, tooltip);
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
