package se.gory_moon.chargers.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import se.gory_moon.chargers.inventory.ContainerCharger;
import se.gory_moon.chargers.tile.TileEntityCharger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class CommonProxy implements IGuiHandler{

    public static final int GUI_CHARGER = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case GUI_CHARGER:
                if (tile instanceof TileEntityCharger)
                    return new ContainerCharger(player, (TileEntityCharger) tile);
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public Map<Integer, ResourceLocation> getItemModelMap(Item item) {
        return Collections.emptyMap();
    }

    public void registerDefaultItemRenderer(Item item) {

    }
}
