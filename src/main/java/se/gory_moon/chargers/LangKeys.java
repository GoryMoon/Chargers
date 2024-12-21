package se.gory_moon.chargers;

public enum LangKeys {
    CHAT_WIRELESS_CHARGER_INFO("chat.wireless_charger.info"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_DISABLED("chat.disabled"),
    CHAT_STORED_INFO("chat.stored.info"),
    CHAT_STORED_INFINITE_INFO("chat.stored.infinite.info"),

    GUI_ENERGY("gui.energy"),
    GUI_ENERGY_INFINITE("gui.energy.infinite"),
    GUI_MAX_IN("gui.max_in"),
    GUI_MAX_OUT("gui.max_out"),
    GUI_IO("gui.io"),
    GUI_DETAILS_IN("gui.details.in"),
    GUI_DETAILS_OUT("gui.details.out"),
    GUI_IO_MORE("gui.io.more"),

    PACK_DESCRIPTION("pack.player_mobs.description");


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
