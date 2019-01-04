package se.gory_moon.chargers.handler;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.compat.Baubles;
import se.gory_moon.chargers.lib.ModInfo;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class WirelessHandler {

    public static WirelessHandler INSTANCE = new WirelessHandler();
    private Int2ObjectMap<ObjectSet<BlockPos>> dimensionChargers = new Int2ObjectOpenHashMap<>();

    public void register(TileEntityWirelessCharger charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getWorld());
        chargers.add(charger.getPos().toImmutable());
    }

    public void unRegister(TileEntityWirelessCharger charger) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(charger.getWorld());
        chargers.remove(charger.getPos().toImmutable());
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.START || event.player.isSpectator()) {
            return;
        }
        INSTANCE.chargeItems(event.player);
    }

    public void chargeItems(EntityPlayer player) {
        ObjectSet<BlockPos> chargers = getDimensionChargers(player.world);
        if (chargers.isEmpty()) return;

        BlockPos playerPos = player.getPosition();
        for (Iterator<BlockPos> iterator = chargers.iterator(); iterator.hasNext();) {
            BlockPos pos = iterator.next();
            TileEntityWirelessCharger charger = getCharger(player.world, pos);
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

    private TileEntityWirelessCharger getCharger(IBlockAccess world, BlockPos pos) {
        if (((World) world).isBlockLoaded(pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityWirelessCharger)
                return (TileEntityWirelessCharger) te;
        }
        return null;
    }

    private boolean chargeItems(EntityPlayer player, TileEntityWirelessCharger charger) {
        charger.updateAvailable();
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

    private ObjectSet<BlockPos> getDimensionChargers(World world) {
        return dimensionChargers.computeIfAbsent(world.provider.getDimension(), integer -> new ObjectOpenHashSet<>());
    }
}
