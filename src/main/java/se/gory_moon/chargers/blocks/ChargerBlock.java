package se.gory_moon.chargers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.tile.ChargerTileEntity;
import se.gory_moon.chargers.tile.TileRegistry;

import javax.annotation.Nullable;

public class ChargerBlock extends EnergyBlock {

    public ChargerBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isRemote)
            return ActionResultType.SUCCESS;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ChargerTileEntity) {
            if (player.isSneaking())
                return ActionResultType.FAIL;

            player.openContainer((ChargerTileEntity) tileEntity);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isRemote) {
            TileEntity aTile = world.getTileEntity(pos);
            if (aTile instanceof ChargerTileEntity) {
                ChargerTileEntity tile = (ChargerTileEntity)aTile;
                for(int i = 0; i < tile.inventoryHandler.getSlots(); ++i) {
                    ItemStack stack = tile.inventoryHandler.getStackInSlot(i);
                    if (!stack.isEmpty())
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof ChargerTileEntity) {
                ((ChargerTileEntity)tileentity).setCustomName(stack.getDisplayName());
            }
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        ChargerTileEntity charger = TileRegistry.CHARGER_TE.create();
        charger.setTier(this == BlockRegistry.CHARGER_BLOCK_T1.get() ? Tier.I: this == BlockRegistry.CHARGER_BLOCK_T2.get() ? Tier.II: Tier.III);
        return charger;
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
