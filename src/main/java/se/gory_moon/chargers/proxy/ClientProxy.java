package se.gory_moon.chargers.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import se.gory_moon.chargers.client.GuiCharger;
import se.gory_moon.chargers.items.ItemRegistry;
import se.gory_moon.chargers.tile.TileEntityCharger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientProxy extends CommonProxy {

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case GUI_CHARGER:
                if (tile instanceof TileEntityCharger)
                    return new GuiCharger(player.inventory, (TileEntityCharger) tile);
                break;
        }
        return null;
    }

    @Override
    public Map<Integer, ResourceLocation> getItemModelMap(Item item) {
        Map<Integer, ResourceLocation> map = new HashMap<>();
        if (item instanceof ItemRegistry.IMultipleItemModelDefinition) {
            for (Map.Entry<Integer, ResourceLocation> model : ((ItemRegistry.IMultipleItemModelDefinition) item).getModels().entrySet()) {
                map.put(model.getKey(), new ModelResourceLocation(model.getValue(), "inventory"));
            }
        } else
            map.put(0, new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
        return map;
    }

    @Override
    public void registerDefaultItemRenderer(Item item) {
        Map<Integer, ResourceLocation> map = this.getItemModelMap(item);
        for(Map.Entry<Integer, ResourceLocation> entry : map.entrySet()) {
            ModelLoader.setCustomModelResourceLocation(item, entry.getKey(), (ModelResourceLocation) entry.getValue());
        }
    }
}
