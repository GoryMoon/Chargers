package se.gory_moon.chargers;

public enum LangKeys {
    CHAT_WIRELESS_CHARGER_INFO("chat.wireless_charger.info"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_DISABLED("chat.disabled"),
    CHAT_STORED_INFO("chat.stored.info"),

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
