package se.gory_moon.chargers.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.block.WirelessChargerBlock;

public class ChargerBlockStateProvider extends BlockStateProvider {

    public ChargerBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlockRegistry.CHARGER_BLOCK_T1.get(),
                models().cubeBottomTop("charger_tier_1",
                        modLoc("block/charger_tier_1_side"),
                        modLoc("block/charger_tier_1"),
                        modLoc("block/charger_tier_1_top"))
        );

        simpleBlock(BlockRegistry.CHARGER_BLOCK_T2.get(),
                models().cubeBottomTop("charger_tier_2",
                        modLoc("block/charger_tier_2_side"),
                        modLoc("block/charger_tier_2"),
                        modLoc("block/charger_tier_2_top"))
        );

        simpleBlock(BlockRegistry.CHARGER_BLOCK_T3.get(),
                models().cubeBottomTop("charger_tier_3",
                        modLoc("block/charger_tier_3_side"),
                        modLoc("block/charger_tier_3"),
                        modLoc("block/charger_tier_3_top"))
        );

        simpleBlock(BlockRegistry.CHARGER_BLOCK_T4.get(),
                models().cubeBottomTop("charger_tier_4",
                        modLoc("block/charger_tier_4_side"),
                        modLoc("block/charger_tier_4"),
                        modLoc("block/charger_tier_4_top"))
        );

        simpleBlock(BlockRegistry.CHARGER_BLOCK_CREATIVE.get(),
                models().cubeBottomTop("charger_creative",
                        modLoc("block/charger_creative_side"),
                        modLoc("block/charger_creative"),
                        modLoc("block/charger_creative_top"))
        );

        getVariantBuilder(BlockRegistry.WIRELESS_CHARGER.get())
                .partialState().with(WirelessChargerBlock.POWERED, false)
                .modelForState().modelFile(models().cubeAll("wireless_charger_disabled", modLoc("block/wireless_charger_disabled"))).addModel()
                .partialState().with(WirelessChargerBlock.POWERED, true)
                .modelForState().modelFile(models().cubeAll("wireless_charger_enabled", modLoc("block/wireless_charger_enabled"))).addModel();
    }
}
