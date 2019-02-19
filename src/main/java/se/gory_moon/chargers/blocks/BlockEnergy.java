package se.gory_moon.chargers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.items.ItemChargerBlock;
import se.gory_moon.chargers.items.ItemWirelessChargerBlock;
import se.gory_moon.chargers.power.CustomEnergyStorage;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;
import se.gory_moon.chargers.tile.TileEntityEnergyHolder;

import javax.annotation.Nullable;

public abstract class BlockEnergy extends Block {

    public BlockEnergy(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
            ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));

            if (te instanceof TileEntityEnergyHolder) {
                TileEntityEnergyHolder energyBlock = (TileEntityEnergyHolder) te;
                CustomItemEnergyStorage.getOrCreateTag(drop).setInteger("Energy", energyBlock.storage.getEnergyStored());
            }

            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if ((stack.getItem() instanceof ItemWirelessChargerBlock || stack.getItem() instanceof ItemChargerBlock) && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            TileEntity tileentity = world.getTileEntity(pos);
            IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (tileentity instanceof TileEntityEnergyHolder && capability instanceof CustomEnergyStorage) {
                ((TileEntityEnergyHolder) tileentity).storage.readFromNBT(((CustomEnergyStorage) capability).writeToNBT(new NBTTagCompound()));
            }
        }
    }
}
