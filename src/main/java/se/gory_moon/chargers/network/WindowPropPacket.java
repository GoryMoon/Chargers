package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import se.gory_moon.chargers.inventory.ChargerMenu;

import java.util.List;
import java.util.function.Supplier;

public class WindowPropPacket {

    private final int containerId;
    private final List<SyncPair> data;

    public WindowPropPacket(FriendlyByteBuf buf) {
        this(buf.readUnsignedByte(), buf.readList(byteBuf -> new SyncPair(byteBuf.readShort(), byteBuf.readLong())));
    }

    public WindowPropPacket(int containerId, List<SyncPair> data) {
        this.containerId = containerId;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(containerId);
        buf.writeCollection(data, (byteBuf, p) -> p.write(byteBuf));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu.containerId == containerId) {
                data.forEach(p -> ((ChargerMenu) mc.player.containerMenu).setData(p.id, p.data));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public record SyncPair(int id, long data) {
        public void write(FriendlyByteBuf buf) {
            buf.writeShort(id);
            buf.writeLong(data);
        }
    }

}
