package se.gory_moon.chargers;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.chargers.blocks.BlockRegistry;
import se.gory_moon.chargers.items.ItemRegistry;
import se.gory_moon.chargers.tile.TileRegistry;

@Mod(Constants.MOD_ID)
public class ChargersMod {

    public static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> {
        Registrate reg = Registrate.create(Constants.MOD_ID);
        reg.addRawLang("container.chargers.charger_t1", "Charger Tier I");
        reg.addRawLang("container.chargers.charger_t2", "Charger Tier II");
        reg.addRawLang("container.chargers.charger_t3", "Charger Tier III");

        reg.addRawLang("chat.chargers.wireless_charger.info", "Status: %s, Power: %s/%s FE");
        reg.addRawLang("chat.chargers.enabled", "Enabled");
        reg.addRawLang("chat.chargers.disabled", "Disabled");
        reg.addRawLang("chat.chargers.stored.info", "Power: %s/%s FE");

        reg.addRawLang("tooltip.chargers.wireless_charger", "Can be disabled with redstone power");

        reg.addRawLang("gui.chargers.energy", "%s/%s FE");
        reg.addRawLang("gui.chargers.max_in", "Max In: %s FE/t");
        reg.addRawLang("gui.chargers.max_out", "Max Out: %s FE/t");
        reg.addRawLang("gui.chargers.io", "I/O: %s FE/t");
        return reg;
    });
    public static final Logger LOGGER = LogManager.getLogger();

    public ChargersMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::onFingerprintViolation);
        BlockRegistry.init();
        TileRegistry.init();
        ItemRegistry.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configs.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.commonSpec);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.get();
    }

    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

}
