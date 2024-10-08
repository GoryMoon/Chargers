package se.gory_moon.chargers.block.entity;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.client.ChargerScreen;
import se.gory_moon.chargers.inventory.ChargerMenu;

import static se.gory_moon.chargers.Constants.CHARGER_BLOCK_ENTITY;
import static se.gory_moon.chargers.Constants.WIRELESS_CHARGER_BLOCK_ENTITY;
import static se.gory_moon.chargers.block.BlockRegistry.*;

public final class BlockEntityRegistry {
    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();

    public static final BlockEntityEntry<ChargerBlockEntity> CHARGER_BE = REGISTRATE.object(CHARGER_BLOCK_ENTITY)
            .blockEntity(ChargerBlockEntity::new)
            .validBlocks(CHARGER_BLOCK_T1, CHARGER_BLOCK_T2, CHARGER_BLOCK_T3, CHARGER_BLOCK_T4, CHARGER_BLOCK_CREATIVE)
            .register();

    public static final BlockEntityEntry<WirelessChargerBlockEntity> WIRELESS_CHARGER_BE = BlockEntityEntry.cast(REGISTRATE.get(WIRELESS_CHARGER_BLOCK_ENTITY, ForgeRegistries.Keys.BLOCK_ENTITY_TYPES));

    public static final MenuEntry<ChargerMenu> CHARGER_CONTAINER = REGISTRATE.object(Constants.CHARGER_CONTAINER)
            .menu(ChargerMenu::new, () -> ChargerScreen::new)
            .register();

    private BlockEntityRegistry() {}
    public static void init() {}
}
