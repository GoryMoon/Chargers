package se.gory_moon.chargers.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.chargers.blocks.BlockRegistry.ICustomItemBlock;
import se.gory_moon.chargers.items.ItemWirelessChargerBlock;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class BlockWirelessCharger extends BlockEnergy implements ICustomItemBlock {

    public static PropertyBool POWERED = PropertyBool.create("powered");

    public BlockWirelessCharger() {
        super(Material.IRON, MapColor.GRAY);
        setHardness(5);
        setResistance(10);
        setCreativeTab(CreativeTabs.REDSTONE);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWirelessCharger) {
            return state.withProperty(POWERED, ((TileEntityWirelessCharger) tileEntity).canCharge());
        }
        return state;
    }

    @Nonnull
    @Override
    public ItemBlock getItemBlock() {
        return new ItemWirelessChargerBlock(this);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWirelessCharger) {
            if (player.isSneaking())
                return false;
            TileEntityWirelessCharger tile = (TileEntityWirelessCharger) tileEntity;
            boolean powered = ((TileEntityWirelessCharger) tileEntity).isPowered();
            ITextComponent status = new TextComponentTranslation("chat.chargers." + (powered ? "disabled": "enabled")).setStyle(new Style().setColor(powered ? TextFormatting.RED: TextFormatting.GREEN));
            NumberFormat format = NumberFormat.getInstance();
            player.sendStatusMessage(new TextComponentTranslation("chat.chargers.wireless_charger.info",  status, format.format(tile.storage.getEnergyStored()), format.format(tile.storage.getMaxEnergyStored())), true);
        }

        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(POWERED, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("tooltip.chargers.wireless_charger"));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityWirelessCharger();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
