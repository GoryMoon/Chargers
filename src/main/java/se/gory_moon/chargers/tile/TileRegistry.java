package se.gory_moon.chargers.tile;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.client.ChargerScreen;
import se.gory_moon.chargers.inventory.ContainerCharger;

import static se.gory_moon.chargers.Constants.CHARGER_TILE;
import static se.gory_moon.chargers.Constants.WIRELESS_CHARGER_TILE;
import static se.gory_moon.chargers.blocks.BlockRegistry.*;

public class TileRegistry {
    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();

    public static final TileEntityEntry<ChargerTileEntity> CHARGER_TE = REGISTRATE.object(CHARGER_TILE)
            .tileEntity(ChargerTileEntity::new)
            .validBlocks(() -> CHARGER_BLOCK_T1.get(), () -> CHARGER_BLOCK_T2.get(), () -> CHARGER_BLOCK_T3.get())
            .register();

    public static final TileEntityEntry<WirelessChargerTileEntity> WIRELESS_CHARGER_TE = TileEntityEntry.cast(REGISTRATE.get(WIRELESS_CHARGER_TILE, TileEntityType.class));


    public static final RegistryEntry<ContainerType<ContainerCharger>> CHARGER_CONTAINER = REGISTRATE.object(Constants.CHARGER_CONTAINER)
            .container(ContainerCharger::new, () -> ChargerScreen::new)
            .register();

    private TileRegistry() {}
    public static void init() {}
}
