package se.gory_moon.chargers.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.compat.Baubles;
import se.gory_moon.chargers.lib.ModInfo;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class WirelessHandler {

    public static WirelessHandler INSTANCE = new WirelessHandler();
    private Map<Integer, Map<BlockPos, TileEntityWirelessCharger>> dimensionChargers = new HashMap<>();

    public void register(TileEntityWirelessCharger charger) {
        Map<BlockPos, TileEntityWirelessCharger> chargers = getDimensionChargers(charger.getWorld());
        chargers.put(charger.getPos(), charger);
    }

    public void unRegister(TileEntityWirelessCharger charger) {
        Map<BlockPos, TileEntityWirelessCharger> chargers = getDimensionChargers(charger.getWorld());
        chargers.remove(charger.getPos());
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || event.player.isSpectator()) {
            return;
        }
        INSTANCE.chargeItems(event.player);
    }

    public void chargeItems(EntityPlayer player) {
        Map<BlockPos, TileEntityWirelessCharger> chargers = getDimensionChargers(player.world);
        if (chargers.isEmpty()) return;

        BlockPos playerPos = player.getPosition();
        for (TileEntityWirelessCharger charger: chargers.values()) {
            if (charger.canCharge() && inRange(charger.getPos(), playerPos)) {
                if (chargeItems(player, charger))
                    return;
            }
        }
    }

    private boolean chargeItems(EntityPlayer player, TileEntityWirelessCharger charger) {
        boolean result = charger.chargeItems(player.inventory.armorInventory);
        result |= charger.chargeItems(player.inventory.mainInventory);
        result |= charger.chargeItems(player.inventory.offHandInventory);
        result |= Baubles.INSTANCE.chargeItems(player, charger);
        if (result)
            player.inventoryContainer.detectAndSendChanges();
        return result;
    }

    private boolean inRange(BlockPos a, BlockPos b) {
        int range = Configs.chargers.wireless.wirelessRange;
        int dx = a.getX() - b.getX();
        if (dx > range || dx < -range) return false;
        int dz = a.getZ() - b.getZ();
        if (dz > range || dz < -range) return false;
        int dy = a.getY() - b.getY();
        return (dx * dx + dy * dy + dz * dz) <= (range * range);
    }

    private Map<BlockPos, TileEntityWirelessCharger> getDimensionChargers(World world) {
        return dimensionChargers.computeIfAbsent(world.provider.getDimension(), integer -> new HashMap<>());
    }
}
