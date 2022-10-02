package se.gory_moon.chargers.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.WirelessChargerBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class WirelessChargerBlock extends EnergyBlock {

    public static BooleanProperty POWERED = BooleanProperty.create("powered");

    public WirelessChargerBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof WirelessChargerBlockEntity changerEntity && changerEntity.getStorage() != null) {
            if (player.isShiftKeyDown())
                return InteractionResult.FAIL;
            boolean powered = changerEntity.isPowered();
            Component status = Component.translatable((powered ? LangKeys.CHAT_DISABLED.key(): LangKeys.CHAT_ENABLED.key())).setStyle(Style.EMPTY.withColor(powered ? ChatFormatting.RED: ChatFormatting.GREEN));
            player.displayClientMessage(Component.translatable(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(),  status, Utils.formatAndClean(changerEntity.getStorage().getEnergyStored()), Utils.formatAndClean(changerEntity.getStorage().getMaxEnergyStored())), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.WIRELESS_CHARGER_BE.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createEnergyTicker(level, type, BlockEntityRegistry.WIRELESS_CHARGER_BE.get());
    }
}
