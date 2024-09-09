package se.gory_moon.chargers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.ChargerBlockEntity;

import javax.annotation.Nullable;

public class ChargerBlock extends EnergyBlock {

    public ChargerBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChargerBlockEntity) {
            if (player.isShiftKeyDown())
                return InteractionResult.FAIL;

            player.openMenu((ChargerBlockEntity) blockEntity);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChargerBlockEntity chargerEntity) {
                for(int i = 0; i < chargerEntity.inventoryHandler.getSlots(); ++i) {
                    ItemStack stack = chargerEntity.inventoryHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChargerBlockEntity) {
                ((ChargerBlockEntity)blockEntity).setCustomName(stack.getHoverName());
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        ChargerBlockEntity charger = BlockEntityRegistry.CHARGER_BE.create(pos, state);
        charger.setTier(Tier.byBlock(this));
        return charger;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createEnergyTicker(level, type, BlockEntityRegistry.CHARGER_BE.get());
    }

    public enum Tier {
        I(0, () -> Configs.SERVER.tier1),
        II(1, () -> Configs.SERVER.tier2),
        III(2, () -> Configs.SERVER.tier3),
        IV(3, () -> Configs.SERVER.tier4),
        C(4, () -> Configs.SERVER.tier4);

        private final int id;
        private final NonNullSupplier<Configs.Server.Tier> tierSupplier;
        @Nullable
        private Configs.Server.Tier tier = null;
        private static final ChargerBlock.Tier[] ID_LOOKUP = new ChargerBlock.Tier[values().length];

        Tier(int id, NonNullSupplier<Configs.Server.Tier> tierSupplier) {
            this.id = id;
            this.tierSupplier = tierSupplier;
        }

        public int getId() {
            return id;
        }

        public boolean isCreative() {
            return this == C;
        }

        public long getStorage() {
            return getTier().storage.get();
        }

        public long getMaxIn() {
            return getTier().maxInput.get();
        }

        public long getMaxOut() {
            return getTier().maxOutput.get();
        }

        private Configs.Server.Tier getTier() {
            if (tier == null)
                tier = tierSupplier.get();
            return tier;
        }

        public static Tier byID(int id) {
            if (id < 0 || id >= ID_LOOKUP.length) {
                id = 0;
            }

            return ID_LOOKUP[id];
        }

        public static Tier byItem(BlockItem item) {
            return byBlock(item.getBlock());
        }

        public static Tier byBlock(Block block) {
            if (BlockRegistry.CHARGER_BLOCK_T2.is(block))
                return Tier.II;
            else if (BlockRegistry.CHARGER_BLOCK_T3.is(block))
                return Tier.III;
            else if (BlockRegistry.CHARGER_BLOCK_T4.is(block))
                return Tier.IV;
            else if (BlockRegistry.CHARGER_BLOCK_CREATIVE.is(block))
                return Tier.C;

            return Tier.I;
        }

        static {
            for (Tier tier : values()) {
                ID_LOOKUP[tier.getId()] = tier;
            }
        }
    }
}
