package se.gory_moon.chargers.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.ChargerBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ChargerBlock extends EnergyBlock {

    private final Tier tier;

    public ChargerBlock(Tier tier, Block.Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return BlockRegistry.CHARGER_CODEC.value();
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult result) {
        if (!level.isClientSide && player instanceof ServerPlayer)
            player.openMenu(state.getMenuProvider(level, pos));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChargerBlockEntity chargerEntity) {
                var inventoryHandler = chargerEntity.getInventoryHandler();
                for(int i = 0; i < inventoryHandler.getSlots(); ++i) {
                    ItemStack stack = inventoryHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        Tier tier = getTier();
        if (tier.isCreative())
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFINITE_INFO.key()));
        else
            Utils.addEnergyTooltip(stack, tooltip);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createEnergyTicker(level, type, BlockEntityRegistry.CHARGER_BE.get());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChargerBlockEntity(pos, state, tier);
    }

    public Tier getTier() {
        return tier;
    }

    public enum Tier implements StringRepresentable {
        I(0, "tier_1", () -> Configs.SERVER.tier1),
        II(1, "tier_2", () -> Configs.SERVER.tier2),
        III(2, "tier_3", () -> Configs.SERVER.tier3),
        IV(3, "tier_4", () -> Configs.SERVER.tier4),
        C(4, "creative", () -> Configs.SERVER.tier4);

        private final int id;
        private final String name;
        private final Supplier<Configs.Server.Tier> tierSupplier;
        @Nullable
        private Configs.Server.Tier tier = null;
        private static final ChargerBlock.Tier[] ID_LOOKUP = new ChargerBlock.Tier[values().length];

        public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);

        Tier(int id, String name, Supplier<Configs.Server.Tier> tierSupplier) {
            this.id = id;
            this.name = name;
            this.tierSupplier = tierSupplier;
        }

        public int getId() {
            return id;
        }

        public boolean isCreative() {
            return this == C;
        }

        public long getStorage() {
            return getTierConfig().storage.get();
        }

        public long getMaxIn() {
            return getTierConfig().maxInput.get();
        }

        public long getMaxOut() {
            return getTierConfig().maxOutput.get();
        }

        private Configs.Server.Tier getTierConfig() {
            if (tier == null)
                tier = tierSupplier.get();
            return tier;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public static Tier byID(int id) {
            if (id < 0 || id >= ID_LOOKUP.length) {
                id = 0;
            }

            return ID_LOOKUP[id];
        }

        static {
            for (Tier tier : values()) {
                ID_LOOKUP[tier.getId()] = tier;
            }
        }
    }
}
