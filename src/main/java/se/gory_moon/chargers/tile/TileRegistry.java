package se.gory_moon.chargers.tile;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.client.ChargerScreen;
import se.gory_moon.chargers.inventory.ChargerMenu;

import static se.gory_moon.chargers.Constants.CHARGER_TILE;
import static se.gory_moon.chargers.Constants.WIRELESS_CHARGER_TILE;
import static se.gory_moon.chargers.blocks.BlockRegistry.*;

public class TileRegistry {
    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();

    public static final BlockEntityType<ChargerTileEntity> CHARGER_TE = REGISTRATE.object(CHARGER_TILE)
            .blockEntity(ChargerTileEntity::new)
            .validBlocks(() -> CHARGER_BLOCK_T1.get(), () -> CHARGER_BLOCK_T2.get(), () -> CHARGER_BLOCK_T3.get())
            .register();

    public static final BlockEntityType<WirelessChargerBlockEntity> WIRELESS_CHARGER_TE = BlockEntityEntry.cast(REGISTRATE.get(WIRELESS_CHARGER_TILE, BlockEntityType.class));


    public static final RegistryEntry<MenuType<ChargerMenu>> CHARGER_CONTAINER = REGISTRATE.object(Constants.CHARGER_CONTAINER)
            .menu(ChargerMenu::new, () -> ChargerScreen::new)
            .register();

    private TileRegistry() {}
    public static void init() {}
}
