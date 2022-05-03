package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.inventory.ChargerMenu;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargerBlockEntity extends EnergyHolderBlockEntity implements Nameable, MenuProvider {

    public ChargerItemStackHandler inventoryHandler;
    private final LazyOptional<ChargerItemStackHandler> lazyInventory = LazyOptional.of(() -> inventoryHandler);
    private ChargerBlock.Tier tier = ChargerBlock.Tier.I;
    @Nullable
    private Component customName;

    public ChargerBlockEntity(BlockEntityType<ChargerBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        inventoryHandler = new ChargerItemStackHandler();
    }

    public void setTier(ChargerBlock.Tier tier) {
        this.tier = tier;
        setStorage(new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut()));
    }

    @Override
    public void tickServer() {
        if (getLevel() != null && !getLevel().isClientSide) {
            CustomEnergyStorage storage = getStorage();
            if (storage != null) {
                ItemStack charge = inventoryHandler.getStackInSlot(2);
                if (!charge.isEmpty()) {
                    LazyOptional<IEnergyStorage> capability = charge.getCapability(CapabilityEnergy.ENERGY);
                    capability.ifPresent(energyStorage -> {
                        int extractAmount = Math.min(storage.getMaxEnergyStored() - storage.getEnergyStored(), storage.getMaxInput());
                        int transferred = energyStorage.extractEnergy(extractAmount, false);
                        if (transferred > 0) {
                            storage.receiveEnergy(transferred, false);
                        }
                    });
                }

                ItemStack input = inventoryHandler.getStackInSlot(0);
                ItemStack output = inventoryHandler.getStackInSlot(1);
                if (!input.isEmpty() && input.getCount() == 1 && output.isEmpty() && storage.getEnergyStored() > 0) {
                    LazyOptional<IEnergyStorage> capability = input.getCapability(CapabilityEnergy.ENERGY);
                    capability.ifPresent(energyStorage -> {
                        int transferred = energyStorage.receiveEnergy(storage.extractEnergy(storage.getEnergyStored(), true), false);
                        if (transferred > 0) {
                            storage.extractEnergy(transferred, false);
                        }
                        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
                            inventoryHandler.setStackInSlot(1, input);
                            inventoryHandler.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    });
                }
            }
            super.tickServer();
        }
    }

    @Override
    public void load(CompoundTag compound) {
        inventoryHandler.deserializeNBT(compound.getCompound("Inventory"));
        setTier(ChargerBlock.Tier.byID(compound.getInt("Tier")));
        if (compound.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventoryHandler.serializeNBT());
        tag.putInt("Tier", tier.getId());
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return lazyInventory.cast();
        return super.getCapability(cap, side);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
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
    public Component getDisplayName() {
        //noinspection ConstantConditions
        return hasCustomName() ? getCustomName(): getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player playerEntity) {
        return new ChargerMenu(BlockEntityRegistry.CHARGER_CONTAINER.get(), containerId, playerInventory, inventoryHandler, energyData, ContainerLevelAccess.create(getLevel(), getBlockPos()));
    }
}
