package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.compat.ChargeCompat;
import se.gory_moon.chargers.inventory.ChargerMenu;
import se.gory_moon.chargers.power.CustomEnergyStorage;

public class ChargerBlockEntity extends EnergyHolderBlockEntity implements MenuProvider {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int CHARGE_SLOT = 2;

    private final ChargerItemStackHandler inventoryHandler;

    public ChargerBlockEntity(BlockPos pos, BlockState state, ChargerBlock.Tier tier) {
        super(BlockEntityRegistry.CHARGER_BE.get(), pos, state);
        inventoryHandler = new ChargerItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        setTier(tier);
    }

    public ChargerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((ChargerBlock) state.getBlock()).getTier());
    }

    public ChargerItemStackHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public void setTier(ChargerBlock.Tier tier) {
        setStorage(new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut(), tier.isCreative()));
    }

    @Override
    public void tickServer() {
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

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventoryHandler.deserializeNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.merge(inventoryHandler.serializeNBT(registries));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new ChargerMenu(containerId, playerInventory, inventoryHandler, energyData, ContainerLevelAccess.create(level, getBlockPos()));
    }
}
