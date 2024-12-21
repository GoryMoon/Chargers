package se.gory_moon.chargers;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.crafting.RecipeSerializers;
import se.gory_moon.chargers.data.ChargerLanguageProvider;
import se.gory_moon.chargers.item.ItemRegistry;
import se.gory_moon.chargers.network.PacketHandler;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class ChargersMod {


    public ChargersMod(IEventBus modBus, ModContainer container) {

        modBus.addListener(this::setup);
        modBus.addListener(this::buildTabContents);
        modBus.addListener(this::gatherData);

        BlockRegistry.init();
        BlockEntityRegistry.init();
        ItemRegistry.init();

        RecipeSerializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        container.registerConfig(ModConfig.Type.SERVER, Configs.serverSpec);
        container.registerConfig(ModConfig.Type.COMMON, Configs.commonSpec);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(true, new PackMetadataGenerator(packOutput)
                .add(PackMetadataSection.TYPE, new PackMetadataSection(
                        Component.translatable(LangKeys.PACK_DESCRIPTION.key()),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));

        gen.addProvider(event.includeClient(), new ChargerLanguageProvider(packOutput));
    }

    public void buildTabContents(BuildCreativeModeTabContentsEvent event) {

    }
}
