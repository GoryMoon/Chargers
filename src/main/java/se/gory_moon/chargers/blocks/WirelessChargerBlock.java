package se.gory_moon.chargers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.tile.TileRegistry;
import se.gory_moon.chargers.tile.WirelessChargerBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class WirelessChargerBlock extends EnergyBlock {

    public static BooleanProperty POWERED = BooleanProperty.create("powered");

    public WirelessChargerBlock(Block.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isClientSide)
            return ActionResultType.SUCCESS;

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof WirelessChargerBlockEntity) {
            if (player.isShiftKeyDown())
                return ActionResultType.FAIL;
            WirelessChargerBlockEntity tile = (WirelessChargerBlockEntity) tileEntity;
            boolean powered = ((WirelessChargerBlockEntity) tileEntity).isPowered();
            ITextComponent status = new TranslationTextComponent((powered ? LangKeys.CHAT_DISABLED.key(): LangKeys.CHAT_ENABLED.key())).setStyle(Style.EMPTY.withColor(powered ? TextFormatting.RED: TextFormatting.GREEN));
            player.displayClientMessage(new TranslationTextComponent(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(),  status, Utils.formatAndClean(tile.getStorage().getEnergyStored()), Utils.formatAndClean(tile.getStorage().getMaxEnergyStored())), true);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LangKeys.TOOLTIP_WIRELESS_CHARGER_INFO.key()));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileRegistry.WIRELESS_CHARGER_TE.create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
