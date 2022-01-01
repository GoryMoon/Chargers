package se.gory_moon.chargers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.inventory.ChargerMenu;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargerBlockEntity extends EnergyHolderBlockEntity implements Nameable, MenuProvider {

    public CustomItemStackHandler inventoryHandler;
    private final LazyOptional<CustomItemStackHandler> lazyInventory = LazyOptional.of(() -> inventoryHandler);
    private ChargerBlock.Tier tier = ChargerBlock.Tier.I;
    @Nullable
    private Component customName;

    public ChargerBlockEntity(BlockEntityType<ChargerBlockEntity> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
        inventoryHandler = new CustomItemStackHandler(2);
    }

    public void setTier(ChargerBlock.Tier tier) {
        this.tier = tier;
        setStorage(new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut()));
    }

    @Override
    public void tickServer() {
        if (getLevel() != null && !getLevel().isClientSide) {
            ItemStack input = inventoryHandler.getStackInSlot(0);
            ItemStack output = inventoryHandler.getStackInSlot(1);
            if (!input.isEmpty() && input.getCount() == 1 && output.isEmpty() && getStorage().getEnergyStored() > 0) {
                LazyOptional<IEnergyStorage> capability = input.getCapability(CapabilityEnergy.ENERGY);
                capability.ifPresent(energyStorage -> {
                    int transferred = energyStorage.receiveEnergy(getStorage().extractEnergy(getStorage().getEnergyStored(), true), false);
                    if (transferred > 0) {
                        getStorage().extractEnergy(transferred, false);
                    }
                    if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
                        inventoryHandler.setStackInSlot(1, input);
                        inventoryHandler.setStackInSlot(0, ItemStack.EMPTY);
                    }
                });
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
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return lazyInventory.cast();
        return super.getCapability(cap);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        Component name = getCustomName();
        if (name == null) {
            name = switch (tier) {
                case I -> new TranslatableComponent(LangKeys.CONTAINER_CHARGER_T1.key());
                case II -> new TranslatableComponent(LangKeys.CONTAINER_CHARGER_T2.key());
                case III -> new TranslatableComponent(LangKeys.CONTAINER_CHARGER_T3.key());
            };
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
