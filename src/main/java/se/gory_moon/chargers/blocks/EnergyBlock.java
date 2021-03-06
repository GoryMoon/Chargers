package se.gory_moon.chargers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.items.ChargerBlockItem;
import se.gory_moon.chargers.items.WirelessChargerBlockItem;
import se.gory_moon.chargers.power.CustomEnergyStorage;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;
import se.gory_moon.chargers.tile.EnergyHolderTileEntity;

import javax.annotation.Nullable;

public abstract class EnergyBlock extends Block {

    public EnergyBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
            ItemStack drop = new ItemStack(this, 1);

            if (te instanceof EnergyHolderTileEntity) {
                EnergyHolderTileEntity energyBlock = (EnergyHolderTileEntity) te;
                CustomItemEnergyStorage.getOrCreateTag(drop).putInt("Energy", energyBlock.getStorage().getEnergyStored());
            }

            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if ((stack.getItem() instanceof WirelessChargerBlockItem || stack.getItem() instanceof ChargerBlockItem) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
            TileEntity tileentity = world.getTileEntity(pos);
            LazyOptional<IEnergyStorage> capability = stack.getCapability(CapabilityEnergy.ENERGY);

            if (tileentity instanceof EnergyHolderTileEntity) {
                capability.ifPresent(energyStorage -> {
                    if (energyStorage instanceof CustomEnergyStorage) {
                        ((EnergyHolderTileEntity) tileentity).getStorage().readFromNBT(((CustomEnergyStorage) energyStorage).writeToNBT(new CompoundNBT()));
                    }
                });
            }
        }
    }
}
