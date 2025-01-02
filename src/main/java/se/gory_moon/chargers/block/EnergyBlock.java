package se.gory_moon.chargers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.block.entity.EnergyHolderBlockEntity;

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
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        var stack = super.getCloneItemStack(state, target, level, pos, player);

        if ((level.getBlockEntity(pos) instanceof EnergyHolderBlockEntity entity))
            stack.applyComponents(entity.collectComponents());

        return stack;
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

}
