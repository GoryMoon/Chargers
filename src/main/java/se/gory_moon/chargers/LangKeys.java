package se.gory_moon.chargers;

public enum LangKeys {
    CHAT_WIRELESS_CHARGER_INFO("chat.wireless_charger.info"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_DISABLED("chat.disabled"),

    POWER_INFO("misc.power.info"),

    GUI_MAX_IN("gui.max_in"),
    GUI_MAX_OUT("gui.max_out"),
    GUI_IO("gui.io"),
    GUI_DETAILS_IN("gui.details.in"),
    GUI_DETAILS_OUT("gui.details.out"),
    GUI_IO_MORE("gui.io.more"),

    CONFIG_CHARGERS("configuration.chargers"),
    CONFIG_COMPAT("configuration.compat"),
    CONFIG_CURIOS_COMPAT("configuration.curios_compat"),
    CONFIG_MAX_INPUT("configuration.max_input"),
    CONFIG_MAX_OUTPUT("configuration.max_output"),
    CONFIG_STORAGE("configuration.storage"),
    CONFIG_RANGE("configuration.range"),

    CONFIG_WIRELESS("configuration.wireless"),
    CONFIG_TIER_1("configuration.tier_1"),
    CONFIG_TIER_2("configuration.tier_2"),
    CONFIG_TIER_3("configuration.tier_3"),
    CONFIG_TIER_4("configuration.tier_4"),

    TOOLTIP_WIRELESS_CHARGER("tooltip.wireless_charger"),

    CREATIVE_TAB("itemGroup.chargers.chargers", true),
    PACK_DESCRIPTION("pack.chargers.description", true);

    private final String key;

    LangKeys(String key) {
        this(key, false);
    }

    LangKeys(String key, boolean raw) {
        this.key = raw ? key : Constants.MOD_ID + "." + key;
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
