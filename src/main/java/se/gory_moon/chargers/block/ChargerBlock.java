package se.gory_moon.chargers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.ChargerBlockEntity;

import javax.annotation.Nullable;

public class ChargerBlock extends EnergyBlock {

    public ChargerBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ChargerBlockEntity charger = BlockEntityRegistry.CHARGER_BE.create(pos, state);
        charger.setTier(this == BlockRegistry.CHARGER_BLOCK_T1.get() ? Tier.I: this == BlockRegistry.CHARGER_BLOCK_T2.get() ? Tier.II: Tier.III);
        return charger;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createEnergyTicker(level, type, BlockEntityRegistry.CHARGER_BE.get());
    }

    public enum Tier {
        I(0),
        II(1),
        III(2);

        private final int id;
        private static final ChargerBlock.Tier[] ID_LOOKUP = new ChargerBlock.Tier[values().length];

        Tier(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getStorage() {
            return this == ChargerBlock.Tier.I ?
                    Configs.SERVER.tier1.storage.get():
                    this == ChargerBlock.Tier.II ?
                            Configs.SERVER.tier2.storage.get():
                            Configs.SERVER.tier3.storage.get();
        }

        public int getMaxIn() {
            return this == ChargerBlock.Tier.I ?
                    Configs.SERVER.tier1.maxInput.get():
                    this == ChargerBlock.Tier.II ?
                            Configs.SERVER.tier2.maxInput.get():
                            Configs.SERVER.tier3.maxInput.get();
        }

        public int getMaxOut() {
            return this == ChargerBlock.Tier.I ?
                    Configs.SERVER.tier1.maxOutput.get():
                    this == ChargerBlock.Tier.II ?
                            Configs.SERVER.tier2.maxOutput.get():
                            Configs.SERVER.tier3.maxOutput.get();
        }

        public static ChargerBlock.Tier byID(int id) {
            if (id < 0 || id >= ID_LOOKUP.length) {
                id = 0;
            }

            return ID_LOOKUP[id];
        }

        static {
            for (ChargerBlock.Tier tier : values()) {
                ID_LOOKUP[tier.getId()] = tier;
            }
        }
    }
}
