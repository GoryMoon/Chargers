package se.gory_moon.chargers.client;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.inventory.ChargerMenu;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TextureHandler {

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre evt)
    {
        if(!evt.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
            return;

        evt.addSprite(ChargerMenu.EMPTY_CHARGE_SLOT);
    }
}
