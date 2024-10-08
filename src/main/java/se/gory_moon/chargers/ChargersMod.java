package se.gory_moon.chargers;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.crafting.RecipeSerializers;
import se.gory_moon.chargers.item.ItemRegistry;
import se.gory_moon.chargers.network.PacketHandler;

@Mod(Constants.MOD_ID)
public class ChargersMod {

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(Constants.MOD_ID));

    public ChargersMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();
        RecipeSerializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
            prov.addTooltip(BlockRegistry.WIRELESS_CHARGER, "Can be disabled with redstone power");
            prov.add(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(), "Status: %s, Power: %s/%s FE");
            prov.add(LangKeys.CHAT_ENABLED.key(), "Enabled");
            prov.add(LangKeys.CHAT_DISABLED.key(), "Disabled");
            prov.add(LangKeys.CHAT_STORED_INFO.key(), "Power: %s/%s FE");
            prov.add(LangKeys.CHAT_STORED_INFINITE_INFO.key(), "Power: ∞ FE");

            prov.add(LangKeys.GUI_ENERGY.key(), "%s/%s FE");
            prov.add(LangKeys.GUI_ENERGY_INFINITE.key(), "∞ FE");
            prov.add(LangKeys.GUI_MAX_IN.key(), "Max In: %s FE/t");
            prov.add(LangKeys.GUI_MAX_OUT.key(), "Max Out: %s FE/t");
            prov.add(LangKeys.GUI_IO.key(), "I/O: %s FE/t");
            prov.add(LangKeys.GUI_IO_MORE.key(), "Hold shift for more I/O details");
            prov.add(LangKeys.GUI_DETAILS_IN.key(), "In: %s FE/t");
            prov.add(LangKeys.GUI_DETAILS_OUT.key(), "Out: %s FE/t");
        });
    }
}
