package se.gory_moon.chargers;

public enum LangKeys {
    CONTAINER_CHARGER_T1("container.charger_t1"),
    CONTAINER_CHARGER_T2("container.charger_t2"),
    CONTAINER_CHARGER_T3("container.charger_t3"),

    CHAT_WIRELESS_CHARGER_INFO("chat.wireless_charger.info"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_DISABLED("chat.disabled"),
    CHAT_STORED_INFO("chat.stored.info"),

    TOOLTIP_WIRELESS_CHARGER_INFO("tooltip.wireless_charger"),

    GUI_ENERGY("gui.energy"),
    GUI_MAX_IN("gui.max_in"),
    GUI_MAX_OUT("gui.max_out"),
    GUI_IO("gui.io");


    private final String key;

    LangKeys(String key) {
        this.key = Constants.MOD_ID + "." + key;
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
