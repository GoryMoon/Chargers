package se.gory_moon.chargers.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.blocks.BlockRegistry.ICustomItemBlock;
import se.gory_moon.chargers.blocks.BlockRegistry.ISubtypeItemBlockModelDefinition;
import se.gory_moon.chargers.items.ItemChargerBlock;
import se.gory_moon.chargers.proxy.CommonProxy;
import se.gory_moon.chargers.tile.TileEntityCharger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCharger extends BlockEnergy implements ISubtypeItemBlockModelDefinition, ICustomItemBlock {

    public static PropertyEnum<Tier> TIERS = PropertyEnum.create("tier", Tier.class);

    public BlockCharger() {
        super(Material.IRON, MapColor.GRAY);
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(5);
        setResistance(10);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.getValue(TIERS).getColor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityCharger) {
            if (player.isSneaking())
                return false;
            player.openGui(ChargersMod.INSTANCE, CommonProxy.GUI_CHARGER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity aTile = world.getTileEntity(pos);
            if (aTile instanceof TileEntityCharger) {
                TileEntityCharger tile = (TileEntityCharger)aTile;
                for(int i = 0; i < tile.inventoryHandler.getSlots(); ++i) {
                    ItemStack stack = tile.inventoryHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        spawnAsEntity(world, pos, stack);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    public String getLocalizedName() {
        return I18n.format(this.getTranslationKey() + "." + Tier.I.getName() + ".name");
    }

    @Nonnull
    @Override
    public ItemBlock getItemBlock() {
        return new ItemChargerBlock(this, this, stack -> Tier.byMetadata(stack.getMetadata()).getName());
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Tier tier: Tier.values())
            items.add(new ItemStack(this, 1, tier.getMeta()));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIERS).getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TIERS, Tier.byMetadata(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return  new BlockStateContainer(this, TIERS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCharger(state.getValue(TIERS));
    }

    @Override
    public int getSubtypeNumber() {
        return 3;
    }

    @Override
    public String getSubtypeName(int meta) {
        return "%s_" + Tier.byMetadata(meta).getName();
    }

    public enum Tier implements IStringSerializable {
        I("tier_1", 0, MapColor.GRAY),
        II("tier_2", 1, MapColor.GOLD),
        III("tier_3", 2, MapColor.CYAN);

        private String unloc;
        private int meta;
        private MapColor color;

        private static final BlockCharger.Tier[] META_LOOKUP = new BlockCharger.Tier[values().length];

        Tier(String unloc, int meta, MapColor color) {
            this.unloc = unloc;
            this.meta = meta;
            this.color = color;
        }

        public int getMeta() {
            return meta;
        }

        public MapColor getColor() {
            return color;
        }

        @Override
        public String getName() {
            return unloc;
        }

        public int getStorage() {
            return this == BlockCharger.Tier.I ? Configs.chargers.tier1.storage: this == BlockCharger.Tier.II ? Configs.chargers.tier2.storage: Configs.chargers.tier3.storage;
        }

        public int getMaxIn() {
            return this == BlockCharger.Tier.I ? Configs.chargers.tier1.maxInput: this == BlockCharger.Tier.II ? Configs.chargers.tier2.maxInput: Configs.chargers.tier3.maxInput;
        }

        public int getMaxOut() {
            return this == BlockCharger.Tier.I ? Configs.chargers.tier1.maxOutput: this == BlockCharger.Tier.II ? Configs.chargers.tier2.maxOutput: Configs.chargers.tier3.maxOutput;
        }

        public static BlockCharger.Tier byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        static {
            for (BlockCharger.Tier tier : values()) {
                META_LOOKUP[tier.getMeta()] = tier;
            }
        }
    }
}
