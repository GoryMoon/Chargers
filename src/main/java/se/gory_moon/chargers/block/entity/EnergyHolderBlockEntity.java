package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.inventory.ChargerData;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public abstract class EnergyHolderBlockEntity extends BlockEntity {

    public static final String STORAGE_TAG = "Storage";

    @Nullable
    private CustomEnergyStorage storage = null;

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
        invalidateCapabilities();
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
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (storage != null)
            storage.deserializeNBT(registries, tag.getCompound(STORAGE_TAG));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (storage != null)
            tag.put(STORAGE_TAG, storage.serializeNBT(registries));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
        // TODO test this
        //return saveWithoutMetadata(registries);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, EnergyHolderBlockEntity blockEntity) {
        blockEntity.tickServer();
    }
}
