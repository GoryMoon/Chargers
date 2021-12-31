package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindowPropPacket {
    private final int windowId;
    private final int id;
    private final int value;

    public WindowPropPacket(int windowId, int property, int value) {
        this.windowId = windowId;
        this.id = property;
        this.value = value;
    }

    public WindowPropPacket(FriendlyByteBuf buff) {
        windowId = buff.readUnsignedByte();
        id = buff.readShort();
        value = buff.readInt();
    }

    public void encode(FriendlyByteBuf buff) {
        buff.writeByte(windowId);
        buff.writeShort(id);
        buff.writeInt(value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu.containerId == windowId) {
                mc.player.containerMenu.setData(id, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
