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
        add(BlockRegistry.CHARGER_BLOCK_T1.get(), "Charger Tier I");
        add(BlockRegistry.CHARGER_BLOCK_T2.get(), "Charger Tier II");
        add(BlockRegistry.CHARGER_BLOCK_T3.get(), "Charger Tier III");
        add(BlockRegistry.CHARGER_BLOCK_T4.get(), "Charger Tier IV");
        add(BlockRegistry.CHARGER_BLOCK_CREATIVE.get(), "Creative Charger");
        add(BlockRegistry.WIRELESS_CHARGER.get(), "Wireless Charger");

        add(LangKeys.TOOLTIP_WIRELESS_CHARGER.key(), "Can be disabled with redstone power");
        add(LangKeys.CREATIVE_TAB.key(), "Chargers");

        add(LangKeys.CHAT_WIRELESS_CHARGER_INFO.key(), "Status: %s, Power: %s/%s FE");
        add(LangKeys.CHAT_ENABLED.key(), "Enabled");
        add(LangKeys.CHAT_DISABLED.key(), "Disabled");
        add(LangKeys.CHAT_STORED_INFO.key(), "Power: %s/%s FE");
        add(LangKeys.CHAT_STORED_INFINITE_INFO.key(), "Power: ∞ FE");

        add(LangKeys.GUI_ENERGY.key(), "%s/%s FE");
        add(LangKeys.GUI_ENERGY_INFINITE.key(), "∞ FE");
        add(LangKeys.GUI_MAX_IN.key(), "Max In: %s FE/t");
        add(LangKeys.GUI_MAX_OUT.key(), "Max Out: %s FE/t");
        add(LangKeys.GUI_IO.key(), "I/O: %s FE/t");
        add(LangKeys.GUI_IO_MORE.key(), "Hold shift for more I/O details");
        add(LangKeys.GUI_DETAILS_IN.key(), "In: %s FE/t");
        add(LangKeys.GUI_DETAILS_OUT.key(), "Out: %s FE/t");
    }
}
