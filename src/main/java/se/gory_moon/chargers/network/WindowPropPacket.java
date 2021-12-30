package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class WindowPropPacket {
    private final int windowId;
    private final int property;
    private final int value;

    public WindowPropPacket(int windowId, int property, int value) {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public WindowPropPacket(PacketBuffer buff) {
        windowId = buff.readUnsignedByte();
        property = buff.readShort();
        value = buff.readInt();
    }

    public void encode(PacketBuffer buff) {
        buff.writeByte(windowId);
        buff.writeShort(property);
        buff.writeInt(value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu != null && mc.player.containerMenu.containerId == windowId) {
                mc.player.containerMenu.setData(property, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
