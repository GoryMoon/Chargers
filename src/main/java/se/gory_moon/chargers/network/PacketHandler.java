package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.lib.ModInfo;

public class PacketHandler {

    public static final PacketHandler INSTANCE = new PacketHandler();
    private SimpleNetworkWrapper networkWrapper;

    public void preInit() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);
        networkWrapper.registerMessage(new ClientboundHandler(), MessageUpdatePower.class, 0, Side.CLIENT);
    }

    public void sendTo(IMessage message, EntityPlayerMP player) {
        networkWrapper.sendTo(message, player);
    }

    // see https://github.com/MinecraftForge/MinecraftForge/issues/3677
    public void sendToAllAround(IMessage packet, BlockPos pos, World world) {
        if (!(world instanceof WorldServer)) {
            return;
        }

        WorldServer worldServer = (WorldServer) world;
        PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (Object playerObj : world.playerEntities) {
            if (playerObj instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) playerObj;

                if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
                    sendTo(packet, player);
                }
            }
        }
    }

    public void sendToAllAround(IMessage message, TileEntity te) {
        sendToAllAround(message, te.getPos(), te.getWorld());
    }

    private static class ClientboundHandler implements IMessageHandler<MessageUpdatePower, IMessage> {
        @Override
        public IMessage onMessage(MessageUpdatePower message, MessageContext ctx) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            try {
                if (mc.isCallingFromMinecraftThread()) {
                    message.handle(ctx);
                } else {
                    mc.addScheduledTask(() -> message.handle(ctx));
                }
            } catch (Exception e) {
                ChargersMod.LOG.throwing(e);
            }
            return null;
        }
    }

}
