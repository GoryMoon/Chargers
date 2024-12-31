package se.gory_moon.chargers.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static se.gory_moon.chargers.Constants.MOD_ID;

public record WindowPropPayload(int containerId, List<SyncPair> data) implements CustomPacketPayload {

    public static final Type<WindowPropPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "window_prop"));

    public static final StreamCodec<ByteBuf, WindowPropPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WindowPropPayload::containerId,
            SyncPair.STREAM_CODEC.apply(ByteBufCodecs.list()),
            WindowPropPayload::data,
            WindowPropPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record SyncPair(int id, long data) {
        public static final StreamCodec<ByteBuf, SyncPair> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                SyncPair::id,
                ByteBufCodecs.VAR_LONG,
                SyncPair::data,
                SyncPair::new
        );
    }
}
