package se.gory_moon.chargers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import se.gory_moon.chargers.blocks.BlockCharger;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityCharger extends TileEntityEnergyHolder {

    public final ItemStackHandler inventoryHandler;
    private BlockCharger.Tier tier;

    public TileEntityCharger(BlockCharger.Tier tier) {
        this();
        setTier(tier);
    }

    public TileEntityCharger() {
        super();
        inventoryHandler = new CustomItemStackHandler(2);
    }

    private void setTier(BlockCharger.Tier tier) {
        this.tier = tier;
        storage = new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return !oldState.getBlock().isAssociatedBlock(newSate.getBlock());
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            ItemStack input = inventoryHandler.getStackInSlot(0);
            ItemStack output = inventoryHandler.getStackInSlot(1);
            if (!input.isEmpty() && output.isEmpty() && storage.getEnergyStored() > 0) {
                if (input.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    IEnergyStorage energyStorage = input.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage != null && input.getCount() == 1) {
                        int transferred = energyStorage.receiveEnergy(storage.extractEnergy(storage.getEnergyStored(), true), false);
                        if (transferred > 0) {
                            storage.extractEnergy(transferred, false);
                        }
                        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
                            inventoryHandler.setStackInSlot(1, input.copy());
                            input.shrink(1);
                            inventoryHandler.setStackInSlot(0, input.getCount() <= 0 ? ItemStack.EMPTY: input);
                        }
                    }
                }
            }
            super.update();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        inventoryHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
        setTier(BlockCharger.Tier.byMetadata(compound.getInteger("Tier")));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("Inventory", inventoryHandler.serializeNBT());
        compound.setInteger("Tier", tier.getMeta());
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventoryHandler;
        else
            return super.getCapability(capability, facing);
    }
}
