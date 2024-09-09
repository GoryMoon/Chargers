package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.compat.ChargeCompat;
import se.gory_moon.chargers.inventory.ChargerMenu;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public class ChargerBlockEntity extends EnergyHolderBlockEntity implements Nameable, MenuProvider {

    public static final String CUSTOM_NAME_TAG = "CustomName";
    public static final String INVENTORY_TAG = "Inventory";
    public static final String TIER_TAG = "Tier";
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int CHARGE_SLOT = 2;

    public ChargerItemStackHandler inventoryHandler;
    private final LazyOptional<ChargerItemStackHandler> lazyInventory = LazyOptional.of(() -> inventoryHandler);
    private ChargerBlock.Tier tier = ChargerBlock.Tier.I;

    @Nullable
    private Component customName;

    public ChargerBlockEntity(BlockEntityType<ChargerBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        inventoryHandler = new ChargerItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    public void setTier(ChargerBlock.Tier tier) {
        this.tier = tier;
        setStorage(new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut(), tier.isCreative()));
    }

    @Override
    public void tickServer() {
        if (getLevel() != null && !getLevel().isClientSide) {
            CustomEnergyStorage storage = getStorage();
            if (storage != null) {
                // Try to charge the block with the provided item
                ItemStack charge = inventoryHandler.getStackInSlot(CHARGE_SLOT);
                if (!charge.isEmpty())
                    ChargeCompat.INSTANCE.dischargeItem(charge, storage, this::setChanged);

                // Try to charge the item in the input slot
                ItemStack input = inventoryHandler.getStackInSlot(INPUT_SLOT);
                ItemStack output = inventoryHandler.getStackInSlot(OUTPUT_SLOT);
                if (!input.isEmpty() && input.getCount() == 1 && storage.getLongEnergyStored() > 0) {
                    boolean isFull = ChargeCompat.INSTANCE.chargeItem(input, storage, t -> {
                        storage.extractLongEnergy(t, false);
                        setChanged();
                    });

                    if (isFull && output.isEmpty()) {
                        inventoryHandler.setStackInSlot(OUTPUT_SLOT, input);
                        inventoryHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
                    }
                }
            }
            super.tickServer();
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        inventoryHandler.deserializeNBT(compound.getCompound(INVENTORY_TAG));
        setTier(ChargerBlock.Tier.byID(compound.getInt(TIER_TAG)));
        if (compound.contains(CUSTOM_NAME_TAG, Tag.TAG_STRING))
            this.customName = Component.Serializer.fromJson(compound.getString(CUSTOM_NAME_TAG));

        super.load(compound);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(INVENTORY_TAG, inventoryHandler.serializeNBT());
        tag.putInt(TIER_TAG, tier.getId());
        if (this.customName != null)
            tag.putString(CUSTOM_NAME_TAG, Component.Serializer.toJson(this.customName));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return lazyInventory.cast();
        return super.getCapability(cap, side);
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
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
        return customName != null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        //noinspection ConstantConditions
        return hasCustomName() ? getCustomName() : getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new ChargerMenu(BlockEntityRegistry.CHARGER_CONTAINER.get(), containerId, playerInventory, inventoryHandler, energyData, ContainerLevelAccess.create(getLevel(), getBlockPos()));
    }
}
