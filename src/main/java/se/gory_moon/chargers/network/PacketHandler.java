package se.gory_moon.chargers.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class PacketHandler {

    // Modified version, see https://github.com/MinecraftForge/MinecraftForge/issues/3677
    private static void sendToAllAround(Packet<?> packet, BlockPos pos, World world) {
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
                    player.connection.sendPacket(packet);
                }
            }
        }
    }

    public static void sendToAllAround(Packet<?> message, TileEntity te) {
        sendToAllAround(message, te.getPos(), te.getWorld());
    }

    public static void sendToListeningPlayers(List<IContainerListener> listeners, Packet<?> packet) {
        for (IContainerListener containerListener : listeners) {
            if (containerListener instanceof EntityPlayerMP) {
                ((EntityPlayerMP) containerListener).connection.sendPacket(packet);
            }
        }
    }
}
