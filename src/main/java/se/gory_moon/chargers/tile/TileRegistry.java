package se.gory_moon.chargers.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileRegistry {
    private TileRegistry() { }

    public static void init() {
        registerTileEntity(TileEntityCharger.class, "charger");
        registerTileEntity(TileEntityWirelessCharger.class, "wireless_charger");
    }

    private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
        GameRegistry.registerTileEntity(cls, "tile.fastcharge." + baseName);
    }
}
