package se.gory_moon.chargers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.chargers.blocks.BlockRegistry;
import se.gory_moon.chargers.lib.ModInfo;
import se.gory_moon.chargers.network.MessageUpdatePower;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.proxy.CommonProxy;
import se.gory_moon.chargers.tile.TileRegistry;

@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, certificateFingerprint = ModInfo.FINGERPRINT)
public class ChargersMod {

    public static Logger LOG = LogManager.getLogger(ModInfo.NAME);

    @Mod.Instance(owner = ModInfo.MODID)
    public static ChargersMod INSTANCE;

    @SidedProxy(modId = ModInfo.MODID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
    public static CommonProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);
        PacketHandler.INSTANCE.preInit();

        BlockRegistry.preInit();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        TileRegistry.init();
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

}
