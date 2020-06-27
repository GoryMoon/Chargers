package se.gory_moon.chargers.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import se.gory_moon.chargers.blocks.ChargerBlock;
import se.gory_moon.chargers.inventory.ContainerCharger;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargerTileEntity extends EnergyHolderTileEntity implements INameable, INamedContainerProvider {

    public CustomItemStackHandler inventoryHandler;
    private final LazyOptional<CustomItemStackHandler> lazyInventory = LazyOptional.of(() -> inventoryHandler);
    private ChargerBlock.Tier tier;
    private ITextComponent customName;

    public ChargerTileEntity(TileEntityType<ChargerTileEntity> tileEntityType) {
        super(tileEntityType);
        inventoryHandler = new CustomItemStackHandler(2);
    }

    public void setTier(ChargerBlock.Tier tier) {
        this.tier = tier;
        setStorage(new CustomEnergyStorage(tier.getStorage(), tier.getMaxIn(), tier.getMaxOut()));
    }

    @Override
    public void tick() {
        if (getWorld() != null && !getWorld().isRemote) {
            ItemStack input = inventoryHandler.getStackInSlot(0);
            ItemStack output = inventoryHandler.getStackInSlot(1);
            if (!input.isEmpty() && output.isEmpty() && getStorage().getEnergyStored() > 0) {
                LazyOptional<IEnergyStorage> capability = input.getCapability(CapabilityEnergy.ENERGY);
                capability.ifPresent(energyStorage -> {
                    if (input.getCount() == 1) {
                        int transferred = energyStorage.receiveEnergy(getStorage().extractEnergy(getStorage().getEnergyStored(), true), false);
                        if (transferred > 0) {
                            getStorage().extractEnergy(transferred, false);
                        }
                        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
                            inventoryHandler.setStackInSlot(1, input.copy());
                            input.shrink(1);
                            inventoryHandler.setStackInSlot(0, input.getCount() <= 0 ? ItemStack.EMPTY: input);
                        }
                    }
                });
            }
            super.tick();
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        inventoryHandler.deserializeNBT(compound.getCompound("Inventory"));
        setTier(ChargerBlock.Tier.byID(compound.getInt("Tier")));
        if (compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound.put("Inventory", inventoryHandler.serializeNBT());
        compound.putInt("Tier", tier.getId());
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return compound;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return lazyInventory.cast();
        return super.getCapability(cap);
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    @Override
    public ITextComponent getName() {
        ITextComponent name = getCustomName();
        if (name == null) {
            switch (tier) {
                case I:
                    name = new TranslationTextComponent("container.chargers.charger_t1");
                    break;
                case II:
                    name = new TranslationTextComponent("container.chargers.charger_t2");
                    break;
                case III:
                    name = new TranslationTextComponent("container.chargers.charger_t3");
                    break;
            }
        }
        return name;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? getCustomName(): getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerCharger(TileRegistry.CHARGER_CONTAINER.get(), windowId, playerInventory, inventoryHandler, energyData, IWorldPosCallable.of(getWorld(), getPos()));
    }
}
