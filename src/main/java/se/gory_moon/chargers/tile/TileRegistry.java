package se.gory_moon.chargers.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.gory_moon.chargers.lib.ModInfo;

public class TileRegistry {
    private TileRegistry() { }

    public static void init() {
        registerTileEntity(TileEntityCharger.class, "charger");
        registerTileEntity(TileEntityWirelessCharger.class, "wireless_charger");

        ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(ModInfo.MODID, 1);
        fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityFixer());
    }

    private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
            GameRegistry.registerTileEntity(cls, new ResourceLocation(ModInfo.MODID, baseName));
    }

    private static class TileEntityFixer implements IFixableData {

        @Override
        public int getFixVersion() {
            return 1;
        }

        private boolean needReplacing(String id) {
            return "tile.fastcharge.charger".equals(id) || "tile.fastcharge.wireless_charger".equals(id);
        }

        private String getId(String id) {
            return id.replaceAll("tile.fastcharge.", "");
        }

        @Override
        public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
            String id = compound.getString("id");
            if(needReplacing(id)) {
                compound.setString("id", ModInfo.MODID + ":" + getId(id));
            }
            return compound;
        }
    }
}
