package se.gory_moon.chargers.network;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import se.gory_moon.chargers.Constants;

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
                .consumerMainThread(WindowPropPacket::handle)
                .add();
    }
}
