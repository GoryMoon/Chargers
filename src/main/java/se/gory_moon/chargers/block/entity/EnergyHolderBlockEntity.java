package se.gory_moon.chargers.block.entity;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import se.gory_moon.chargers.compat.bc.BrandonsCoreCompat;
import se.gory_moon.chargers.compat.fn.FluxNetworksCompat;
import se.gory_moon.chargers.inventory.ChargerData;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.HashMap;
import java.util.Map;

public abstract class EnergyHolderBlockEntity extends BlockEntity {

    public static final String STORAGE_TAG = "Storage";

    @Nullable
    private CustomEnergyStorage storage = null;
    private LazyOptional<CustomEnergyStorage> lazyStorage = LazyOptional.of(() -> storage);

    private final Map<Capability<?>, Pair<IEnergyStorage, LazyOptional<IEnergyStorage>>> compatWrappers = new HashMap<>();

    protected final ChargerData energyData = new ChargerData() {
        public long get(int index) {
            return switch (index) {
                case 0 -> storage.getLongEnergyStored();
                case 1 -> storage.getLongMaxEnergyStored();
                case 2 -> storage.getMaxInput();
                case 3 -> storage.getMaxOutput();
                case 4 -> storage.getAverageIn();
                case 5 -> storage.getAverageOut();
                case 6 -> storage.isCreative() ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int index, long value) {
        }

        public int getCount() {
            return 7;
        }
    };

    public EnergyHolderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public void setStorage(CustomEnergyStorage storage) {
        this.storage = storage;
        lazyStorage.invalidate();
        lazyStorage = LazyOptional.of(() -> storage);

        if (BrandonsCoreCompat.INSTANCE.isLoaded()) {
            BrandonsCoreCompat.INSTANCE.createOpWrapper(storage, compatWrappers);
        }

        if (FluxNetworksCompat.INSTANCE.isLoaded()) {
            FluxNetworksCompat.INSTANCE.createFNWrapper(storage, compatWrappers);
        }
    }

    @Override
    public void invalidateCaps() {
        if (lazyStorage != null)
            lazyStorage.invalidate();
        lazyStorage = null;
        compatWrappers.forEach((capability, lazyOptionalPair) -> lazyOptionalPair.second().invalidate());
        compatWrappers.clear();

        super.invalidateCaps();
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
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (storage != null)
            storage.deserializeNBT(compound.getCompound(STORAGE_TAG));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (storage != null)
            tag.put(STORAGE_TAG, storage.serializeNBT());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (BrandonsCoreCompat.INSTANCE.isOpCapability(cap))
            return compatWrappers.get(cap).second().cast();

        if (FluxNetworksCompat.INSTANCE.isOpCapability(cap))
            return compatWrappers.get(cap).second().cast();

        if (cap == ForgeCapabilities.ENERGY)
            return lazyStorage.cast();

        return super.getCapability(cap, side);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, EnergyHolderBlockEntity blockEntity) {
        blockEntity.tickServer();
    }
}
