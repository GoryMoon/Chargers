package se.gory_moon.chargers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import se.gory_moon.chargers.block.entity.EnergyHolderBlockEntity;
import se.gory_moon.chargers.item.ChargerBlockItem;
import se.gory_moon.chargers.item.WirelessChargerBlockItem;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import javax.annotation.Nullable;

public abstract class EnergyBlock extends BaseEntityBlock {

    public EnergyBlock(Block.Properties properties) {
        super(properties);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createEnergyTicker(Level level, BlockEntityType<T> blockEntityTypeIn, BlockEntityType<? extends EnergyHolderBlockEntity> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityTypeIn, blockEntityType, EnergyHolderBlockEntity::tickServer);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack stack) {
        if (!level.isClientSide && !level.restoringBlockSnapshots) {
            ItemStack drop = new ItemStack(this, 1);

            if (entity instanceof EnergyHolderBlockEntity energyBlock) {
                drop.getOrCreateTag().putInt("Energy", energyBlock.getStorage().getEnergyStored());
            }

            popResource(level, pos, drop);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if ((stack.getItem() instanceof WirelessChargerBlockItem || stack.getItem() instanceof ChargerBlockItem) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            LazyOptional<IEnergyStorage> capability = stack.getCapability(CapabilityEnergy.ENERGY);

            if (blockEntity instanceof EnergyHolderBlockEntity) {
                capability.ifPresent(energyStorage -> {
                    if (energyStorage instanceof CustomEnergyStorage) {
                        ((EnergyHolderBlockEntity) blockEntity).getStorage().readFromNBT(((CustomEnergyStorage) energyStorage).writeToNBT(new CompoundTag()));
                    }
                });
            }
        }
    }
}
