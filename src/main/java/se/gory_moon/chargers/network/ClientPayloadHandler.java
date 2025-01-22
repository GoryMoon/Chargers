package se.gory_moon.chargers.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import se.gory_moon.chargers.inventory.ChargerMenu;

public class ClientPayloadHandler {

    public static void handleWindowProp(final WindowPropPayload payload, final IPayloadContext ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.containerMenu.containerId == payload.containerId()) {
            payload.data().forEach(p -> ((ChargerMenu) mc.player.containerMenu).setData(p.id(), p.data()));
        }
    }

}
