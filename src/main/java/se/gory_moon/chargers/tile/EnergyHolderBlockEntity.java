package se.gory_moon.chargers.tile;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyHolderBlockEntity extends BlockEntity {

    private CustomEnergyStorage storage = null;
    private LazyOptional<CustomEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);
    protected final ContainerData energyData = new ContainerData() {
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

        public int getCount() {
            return 6;
        }
    };

    public EnergyHolderBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
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
    public void load(CompoundTag compound) {
        super.load(compound);
        storage.readFromNBT(compound.getCompound("Storage"));
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound).put("Storage", storage.writeToNBT(new CompoundTag()));
        return compound;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return save(super.getUpdateTag());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY)
            return lazyStorage.cast();
        return super.getCapability(cap, side);
    }
}
