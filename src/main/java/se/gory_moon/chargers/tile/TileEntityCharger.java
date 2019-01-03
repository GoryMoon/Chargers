package se.gory_moon.chargers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.blocks.BlockCharger;

import javax.annotation.Nullable;

public class TileEntityCharger extends TileEntity implements ITickable {

    public CustomEnergyStorage storage;
    public final ItemStackHandler inventoryHandler;
    public BlockCharger.Tier tier;

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
        int storageAmount = tier == BlockCharger.Tier.I ? Configs.chargers.tier1.storage: tier == BlockCharger.Tier.II ? Configs.chargers.tier2.storage: Configs.chargers.tier3.storage;
        int maxIn = tier == BlockCharger.Tier.I ? Configs.chargers.tier1.maxInput: tier == BlockCharger.Tier.II ? Configs.chargers.tier2.maxInput: Configs.chargers.tier3.maxInput;
        int maxOut = tier == BlockCharger.Tier.I ? Configs.chargers.tier1.maxOutput: tier == BlockCharger.Tier.II ? Configs.chargers.tier2.maxOutput: Configs.chargers.tier3.maxOutput;
        storage = new CustomEnergyStorage(storageAmount, maxIn, maxOut);
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
            storage.tick();
        }
    }

    public float getEnergyDiff() {
        return storage.getAverageChange();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventoryHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
        setTier(BlockCharger.Tier.byMetadata(compound.getInteger("Tier")));
        storage.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("Inventory", inventoryHandler.serializeNBT());
        compound.setInteger("Tier", tier.getMeta());
        compound = storage.writeToNBT(compound);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) storage;
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventoryHandler;
        else
            return super.getCapability(capability, facing);
    }
}
