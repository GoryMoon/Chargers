package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.inventory.ChargerData;
import se.gory_moon.chargers.item.ChargerDataComponents;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public abstract class EnergyHolderBlockEntity extends BlockEntity implements Nameable {

    public static final String STORAGE_TAG = "Storage";
    public static final String CUSTOM_NAME_TAG = "CustomName";
    @Nullable
    protected Component name;

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
    protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
        name = componentInput.get(DataComponents.CUSTOM_NAME);
        if (storage != null)
            storage.setEnergy(componentInput.getOrDefault(ChargerDataComponents.ENERGY, 0L));
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder components) {
        components.set(DataComponents.CUSTOM_NAME, getCustomName());
        if (storage != null)
            components.set(ChargerDataComponents.ENERGY, storage.getLongEnergyStored());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (storage != null)
            storage.deserializeNBT(registries, tag.getCompound(STORAGE_TAG));

        if (tag.contains(CUSTOM_NAME_TAG, Tag.TAG_STRING))
            name = parseCustomNameSafe(tag.getString(CUSTOM_NAME_TAG), registries);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (storage != null)
            tag.put(STORAGE_TAG, storage.serializeNBT(registries));

        if (name != null)
            tag.putString(CUSTOM_NAME_TAG, Component.Serializer.toJson(name, registries));
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
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, EnergyHolderBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    @Override
    public @NotNull Component getName() {
        Component name = getCustomName();
        if (name == null) {
            name = getBlockState().getBlock().getName();
        }
        return name;
    }

    @Override
    public boolean hasCustomName() {
        return name != null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        //noinspection ConstantConditions
        return hasCustomName() ? getCustomName() : getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return name;
    }
}
