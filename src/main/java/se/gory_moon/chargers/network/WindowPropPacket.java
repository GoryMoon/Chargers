package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WindowPropPacket {
    private final int containerId;
    private final int id;
    private final int value;

    public WindowPropPacket(int containerId, int property, int value) {
        this.containerId = containerId;
        this.id = property;
        this.value = value;
    }

    public WindowPropPacket(FriendlyByteBuf buff) {
        containerId = buff.readUnsignedByte();
        id = buff.readShort();
        value = buff.readInt();
    }

    public void encode(FriendlyByteBuf buff) {
        buff.writeByte(containerId);
        buff.writeShort(id);
        buff.writeInt(value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu.containerId == containerId) {
                mc.player.containerMenu.setData(id, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
