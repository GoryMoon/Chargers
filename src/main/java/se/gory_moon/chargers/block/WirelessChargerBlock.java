package se.gory_moon.chargers.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.WirelessChargerBlockEntity;

import javax.annotation.Nullable;

public class WirelessChargerBlock extends EnergyBlock {

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return BlockRegistry.WIRELESS_CHARGER_CODEC.value();
    }

    public static BooleanProperty POWERED = BooleanProperty.create("powered");

    public WirelessChargerBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult result) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof WirelessChargerBlockEntity changerEntity && changerEntity.getStorage() != null) {
            if (player.isShiftKeyDown())
                return InteractionResult.FAIL;

            boolean powered = changerEntity.isPowered();
            Component status = Component.translatable((powered ? LangKeys.CHAT_DISABLED.key() : LangKeys.CHAT_ENABLED.key()))
                    .setStyle(Style.EMPTY.withColor(powered ? ChatFormatting.RED : ChatFormatting.GREEN));

            var storage = changerEntity.getStorage();
            var text = Component.translatable(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(),
                    status,
                    Utils.formatAndClean(storage.getLongEnergyStored()),
                    Utils.formatAndClean(storage.getLongMaxEnergyStored()));
            player.displayClientMessage(text, true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityRegistry.WIRELESS_CHARGER_BE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createEnergyTicker(level, type, BlockEntityRegistry.WIRELESS_CHARGER_BE.get());
    }
}
