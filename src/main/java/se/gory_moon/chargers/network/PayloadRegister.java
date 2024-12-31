package se.gory_moon.chargers.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PayloadRegister {

    @SubscribeEvent
    public static void onPayloadRegister(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                WindowPropPayload.TYPE,
                WindowPropPayload.STREAM_CODEC,
                ClientPayloadHandler::handleWindowProp
        );
    }
}
