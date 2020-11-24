package se.gory_moon.chargers.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyHolderTileEntity extends TileEntity implements ITickableTileEntity {

    private CustomEnergyStorage storage = null;
    private LazyOptional<CustomEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);
    protected final IIntArray energyData = new IIntArray() {
        public int get(int index) {
            switch(index) {
                case 0:
                    return storage.getEnergyStored();
                case 1:
                    return storage.getMaxEnergyStored();
                case 2:
                    return storage.getMaxInput();
                case 3:
                    return storage.getMaxOutput();
                case 4:
                    return Math.round(storage.getAverageIn());
                case 5:
                    return Math.round(storage.getAverageOut());
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {}

        public int size() {
            return 6;
        }
    };

    public EnergyHolderTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void setStorage(CustomEnergyStorage storage) {
        this.storage = storage;
        lazyStorage.invalidate();
        lazyStorage = LazyOptional.of(() -> storage);
    }

    public CustomEnergyStorage getStorage() {
        return storage;
    }

    @Override
    public void tick() {
        storage.tick();
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        storage.readFromNBT(compound.getCompound("Storage"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound).put("Storage", storage.writeToNBT(new CompoundNBT()));
        return compound;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY)
            return lazyStorage.cast();
        return super.getCapability(cap, side);
    }
}
