package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyHolderBlockEntity extends BlockEntity {

    @Nullable
    private CustomEnergyStorage storage = null;
    private LazyOptional<CustomEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);
    protected final ContainerData energyData = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStored();
                case 1 -> storage.getMaxEnergyStored();
                case 2 -> storage.getMaxInput();
                case 3 -> storage.getMaxOutput();
                case 4 -> Math.round(storage.getAverageIn());
                case 5 -> Math.round(storage.getAverageOut());
                default -> 0;
            };
        }

        public void set(int index, int value) {}

        public int getCount() {
            return 6;
        }
    };

    public EnergyHolderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public void setStorage(CustomEnergyStorage storage) {
        this.storage = storage;
        lazyStorage.invalidate();
        lazyStorage = LazyOptional.of(() -> storage);
    }

    @Nullable
    public CustomEnergyStorage getStorage() {
        return storage;
    }

    public void tickServer() {
        if (storage != null)
            storage.tick();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (storage != null)
            storage.deserializeNBT(compound.getCompound("Storage"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (storage != null)
            tag.put("Storage", storage.serializeNBT());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY)
            return lazyStorage.cast();
        return super.getCapability(cap, side);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, EnergyHolderBlockEntity blockEntity) {
        blockEntity.tickServer();
    }
}
