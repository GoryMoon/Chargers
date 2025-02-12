package se.gory_moon.chargers.handler;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.entity.WirelessChargerBlockEntity;
import se.gory_moon.chargers.compat.Curios;

import javax.annotation.Nullable;
import java.util.Iterator;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class WirelessHandler {

    public static WirelessHandler INSTANCE = new WirelessHandler();
    private final Object2ObjectMap<ResourceLocation, ObjectSet<BlockPos>> dimensionChargers = new Object2ObjectOpenHashMap<>();

    public void register(WirelessChargerBlockEntity charger, Level level) {
        synchronized (dimensionChargers) {
            getDimensionChargers(level).add(new BlockPos(charger.getBlockPos()));
        }
    }

    public void unregister(WirelessChargerBlockEntity charger, @Nullable Level level) {
        if (level == null) return;
        synchronized (dimensionChargers) {
            getDimensionChargers(level).remove(charger.getBlockPos());
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity().level().isClientSide() || event.getEntity().isSpectator()) {
            return;
        }
        INSTANCE.chargeItems(event.getEntity());
    }

    public void chargeItems(Player player) {
        synchronized (dimensionChargers) {
            ObjectSet<BlockPos> chargers = getDimensionChargers(player.level());
            if (chargers.isEmpty())
                return;

            BlockPos playerPos = player.blockPosition();
            for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext(); ) {
                BlockPos pos = iterator.next();
                WirelessChargerBlockEntity charger = getCharger(player.level(), pos);
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
    }

    @Nullable
    private WirelessChargerBlockEntity getCharger(Level level, BlockPos pos) {
        if (level.isAreaLoaded(pos, 1)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof WirelessChargerBlockEntity)
                return (WirelessChargerBlockEntity) blockEntity;
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

    private ObjectSet<BlockPos> getDimensionChargers(Level level) {
        return dimensionChargers.computeIfAbsent(level.dimension().location(), location -> new ObjectOpenHashSet<>());
    }
}
