package se.gory_moon.chargers.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.blocks.BlockRegistry;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static se.gory_moon.chargers.lib.ModInfo.MODID;

@Mod.EventBusSubscriber
public class ItemRegistry {
    public final static Set<Item> ITEMS = new LinkedHashSet<>();

    //Items

    public static void preInit() {
        try {
            for (Field field : ItemRegistry.class.getDeclaredFields()) {
                if (field.get(null) instanceof Item) {
                    Item item = (Item) field.get(null);
                    registerItem(item, field.getName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void registerItem(Item item, String fieldName) {
        ITEMS.add(item);
        String name = fieldName.toLowerCase(Locale.ENGLISH);
        item.setRegistryName(MODID, name).setTranslationKey(MODID + "." + name);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        for (Item item : BlockRegistry.ITEM_BLOCKS) {
            registry.register(item);
        }
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            ChargersMod.proxy.registerDefaultItemRenderer(item);
        }
    }

    public interface IMultipleItemModelDefinition {
        /**
         * @return A map from item meta values to different item models
         */
        @SideOnly(Side.CLIENT)
        Map<Integer, ResourceLocation> getModels();
    }
}
