package se.gory_moon.chargers.handler;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.compat.Curios;
import se.gory_moon.chargers.tile.WirelessChargerBlockEntity;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class WirelessHandler {

    public static WirelessHandler INSTANCE = new WirelessHandler();
    private final Object2ObjectMap<ResourceLocation, ObjectSet<BlockPos>> dimensionChargers = new Object2ObjectOpenHashMap<>();

    public void register(WirelessChargerBlockEntity charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getLevel());
        chargers.add(charger.getBlockPos().immutable());
    }

    public void unRegister(WirelessChargerBlockEntity charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getLevel());
        chargers.remove(charger.getBlockPos().immutable());
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.START || event.player.isSpectator()) {
            return;
        }
        INSTANCE.chargeItems(event.player);
    }

    public void chargeItems(PlayerEntity player) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(player.level);
        if (chargers.isEmpty()) return;

        BlockPos playerPos = player.blockPosition();
        for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext();) {
            BlockPos pos = iterator.next();
            WirelessChargerBlockEntity charger = getCharger(player.level, pos);
            if (charger != null) {
                if (charger.canCharge() && inRange(charger.getBlockPos(), playerPos)) {
                    if (chargeItems(player, charger))
                        return;
                }
            } else {
                iterator.remove();
            }
        }
    }

    private WirelessChargerBlockEntity getCharger(IWorld world, BlockPos pos) {
        if (world.hasChunkAt(pos)) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof WirelessChargerBlockEntity)
                return (WirelessChargerBlockEntity) te;
        }
        return null;
    }

    private boolean chargeItems(Player player, WirelessChargerBlockEntity charger) {
        charger.updateAvailable();
        boolean result = charger.chargeItems(player.getInventory().armor);
        result |= charger.chargeItems(player.getInventory().items);
        result |= charger.chargeItems(player.getInventory().offhand);
        result |= Curios.INSTANCE.chargeItems(player, charger);
        if (result)
            player.inventoryMenu.broadcastChanges();
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
        return dimensionChargers.computeIfAbsent(world.dimension().location(), location -> new ObjectOpenHashSet<>());
    }
}
