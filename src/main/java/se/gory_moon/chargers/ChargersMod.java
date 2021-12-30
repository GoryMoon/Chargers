package se.gory_moon.chargers;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.util.LazyValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.chargers.blocks.BlockRegistry;
import se.gory_moon.chargers.items.ItemRegistry;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.tile.TileRegistry;

@Mod(Constants.MOD_ID)
public class ChargersMod {

    public static final LazyValue<Registrate> REGISTRATE = new LazyValue<>(() -> Registrate.create(Constants.MOD_ID));
    public static final Logger LOGGER = LogManager.getLogger();

    public ChargersMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        BlockRegistry.init();
        TileRegistry.init();
        ItemRegistry.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configs.serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.commonSpec);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.get();
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
    private void gatherData(GatherDataEvent event) {
        getRegistrate().addDataGenerator(ProviderType.LANG, prov -> {
            prov.add(LangKeys.CONTAINER_CHARGER_T1.key(), "Charger Tier I");
            prov.add(LangKeys.CONTAINER_CHARGER_T2.key(), "Charger Tier II");
            prov.add(LangKeys.CONTAINER_CHARGER_T3.key(), "Charger Tier III");

            prov.add(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(), "Status: %s, Power: %s/%s FE");
            prov.add(LangKeys.CHAT_ENABLED.key(), "Enabled");
            prov.add(LangKeys.CHAT_DISABLED.key(), "Disabled");
            prov.add(LangKeys.CHAT_STORED_INFO.key(), "Power: %s/%s FE");

            prov.add(LangKeys.TOOLTIP_WIRELESS_CHARGER_INFO.key(), "Can be disabled with redstone power");

            prov.add(LangKeys.GUI_ENERGY.key(), "%s/%s FE");
            prov.add(LangKeys.GUI_MAX_IN.key(), "Max In: %s FE/t");
            prov.add(LangKeys.GUI_MAX_OUT.key(), "Max Out: %s FE/t");
            prov.add(LangKeys.GUI_IO.key(), "I/O: %s FE/t");
        });
    }
}
