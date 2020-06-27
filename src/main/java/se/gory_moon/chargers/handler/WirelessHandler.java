package se.gory_moon.chargers.handler;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.compat.Curios;
import se.gory_moon.chargers.tile.WirelessChargerTileEntity;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class WirelessHandler {

    public static WirelessHandler INSTANCE = new WirelessHandler();
    private final Int2ObjectMap<ObjectSet<BlockPos>> dimensionChargers = new Int2ObjectOpenHashMap<>();

    public void register(WirelessChargerTileEntity charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getWorld());
        chargers.add(charger.getPos().toImmutable());
    }

    public void unRegister(WirelessChargerTileEntity charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getWorld());
        chargers.remove(charger.getPos().toImmutable());
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.START || event.player.isSpectator()) {
            return;
        }
        INSTANCE.chargeItems(event.player);
    }

    public void chargeItems(PlayerEntity player) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(player.world);
        if (chargers.isEmpty()) return;

        BlockPos playerPos = player.getPosition();
        for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext();) {
            BlockPos pos = iterator.next();
            WirelessChargerTileEntity charger = getCharger(player.world, pos);
            if (charger != null) {
                if (charger.canCharge() && inRange(charger.getPos(), playerPos)) {
                    if (chargeItems(player, charger))
                        return;
                }
            } else {
                iterator.remove();
            }
        }
    }

    private WirelessChargerTileEntity getCharger(IWorld world, BlockPos pos) {
        if (world.isBlockLoaded(pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof WirelessChargerTileEntity)
                return (WirelessChargerTileEntity) te;
        }
        return null;
    }

    private boolean chargeItems(PlayerEntity player, WirelessChargerTileEntity charger) {
        charger.updateAvailable();
        boolean result = charger.chargeItems(player.inventory.armorInventory);
        result |= charger.chargeItems(player.inventory.mainInventory);
        result |= charger.chargeItems(player.inventory.offHandInventory);
        result |= Curios.INSTANCE.chargeItems(player, charger);
        if (result)
            player.container.detectAndSendChanges();
        return result;
    }

    private boolean inRange(BlockPos a, BlockPos b) {
        int range = Configs.SERVER.wireless.range.get();
        int dx = a.getX() - b.getX();
        if (dx > range || dx < -range) return false;
        int dz = a.getZ() - b.getZ();
        if (dz > range || dz < -range) return false;
        int dy = a.getY() - b.getY();
        return (dx * dx + dy * dy + dz * dz) <= (range * range);
    }

    private ObjectSet<BlockPos> getDimensionChargers(World world) {
        return dimensionChargers.computeIfAbsent(world.getDimension().getType().getId(), integer -> new ObjectOpenHashSet<>());
    }
}
