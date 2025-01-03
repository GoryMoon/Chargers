package se.gory_moon.chargers.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.block.BlockRegistry;

import static se.gory_moon.chargers.Constants.MOD_ID;

public class ChargerLanguageProvider extends LanguageProvider {
    public ChargerLanguageProvider(PackOutput packOutput) {
        super(packOutput, MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlock(BlockRegistry.CHARGER_BLOCK_T1, "Charger Tier I");
        addBlock(BlockRegistry.CHARGER_BLOCK_T2, "Charger Tier II");
        addBlock(BlockRegistry.CHARGER_BLOCK_T3, "Charger Tier III");
        addBlock(BlockRegistry.CHARGER_BLOCK_T4, "Charger Tier IV");
        addBlock(BlockRegistry.CHARGER_BLOCK_CREATIVE, "Creative Charger");
        addBlock(BlockRegistry.WIRELESS_CHARGER, "Wireless Charger");

        add(LangKeys.TOOLTIP_WIRELESS_CHARGER, "Can be disabled with redstone power");
        add(LangKeys.CREATIVE_TAB, "Chargers");
        add(LangKeys.PACK_DESCRIPTION, "Chargers mod resources");

        add(LangKeys.CONFIG_CHARGERS, "Chargers");
        add(LangKeys.CONFIG_COMPAT, "Compatability");
        add(LangKeys.CONFIG_CURIOS_COMPAT, "Curios");
        add(LangKeys.CONFIG_MAX_INPUT, "Max Input");
        add(LangKeys.CONFIG_MAX_OUTPUT, "Max Output");
        add(LangKeys.CONFIG_STORAGE, "Storage");
        add(LangKeys.CONFIG_RANGE, "Range");

        add(LangKeys.CONFIG_WIRELESS, "Wireless Charger");
        add(LangKeys.CONFIG_TIER_1, "Charger Tier 1");
        add(LangKeys.CONFIG_TIER_2, "Charger Tier 2");
        add(LangKeys.CONFIG_TIER_3, "Charger Tier 3");
        add(LangKeys.CONFIG_TIER_4, "Charger Tier 4");

        add(LangKeys.CHAT_WIRELESS_CHARGER_INFO, "Status: %s, %s");
        add(LangKeys.CHAT_ENABLED, "Enabled");
        add(LangKeys.CHAT_DISABLED, "Disabled");

        add(LangKeys.POWER_INFO, "Power: %s");
        add(LangKeys.ENERGY_INFINITE, "∞ %s");

        add(LangKeys.GUI_MAX_IN, "Max In: %s");
        add(LangKeys.GUI_MAX_OUT, "Max Out: %s");
        add(LangKeys.GUI_IO, "I/O: %s");
        add(LangKeys.GUI_IO_MORE, "Hold shift for more I/O details");
        add(LangKeys.GUI_DETAILS_IN, "In: %s");
        add(LangKeys.GUI_DETAILS_OUT, "Out: %s");
    }

    private void add(LangKeys key, String text) {
        add(key.key(), text);
    }
}
