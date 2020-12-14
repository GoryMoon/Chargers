package se.gory_moon.chargers.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.IPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import se.gory_moon.chargers.Constants;

import java.util.List;
import java.util.Objects;

public class PacketHandler {

    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(Constants.NET_ID)
            .clientAcceptedVersions(s -> Objects.equals(s, "1"))
            .serverAcceptedVersions(s -> Objects.equals(s, "1"))
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void init() {
        INSTANCE.messageBuilder(WindowPropPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(WindowPropPacket::encode)
                .decoder(WindowPropPacket::new)
                .consumer(WindowPropPacket::handle)
                .add();
    }


    public static void sendToListeningPlayers(List<IContainerListener> listeners, IPacket<?> packet) {
        for (IContainerListener containerListener : listeners) {
            if (containerListener instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) containerListener).connection.sendPacket(packet);
            }
        }
    }
}
