package se.gory_moon.chargers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.client.ChargerScreen;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ChargersModClient {

    public ChargersModClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::registerScreens);

        // Register the configuration screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BlockEntityRegistry.CHARGER_MENU.get(), ChargerScreen::new);
    }
}
