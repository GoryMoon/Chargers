package se.gory_moon.chargers.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import se.gory_moon.chargers.inventory.ContainerCharger;
import se.gory_moon.chargers.lib.ModInfo;
import se.gory_moon.chargers.tile.TileEntityCharger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiCharger extends GuiContainer {

    private TileEntityCharger charger;
    private static final ResourceLocation CHARGER_GUI_TEXTURE = new ResourceLocation(ModInfo.MODID + ":textures/gui/charger.png");

    public GuiCharger(InventoryPlayer inventory, TileEntityCharger tile) {
        super(new ContainerCharger(inventory, tile));
        this.charger = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(CHARGER_GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (charger.storage.getEnergyStored() > 0) {
            int progress = (int)((float)charger.storage.getEnergyStored() / (float) charger.storage.getMaxEnergyStored() * 70);
            drawTexturedModalRect(guiLeft + 58, guiTop + 78 - progress, 0, 236 - progress, 16, progress);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        if (mouseX >= guiLeft + 58 && mouseX <= guiLeft + 74 && mouseY >= guiTop + 8 && mouseY <= guiTop + 78) {
            NumberFormat format = NumberFormat.getInstance();
            List<String> list = new ArrayList<>();
            list.add(I18n.format("gui.chargers.energy", format.format(charger.storage.getEnergyStored()), format.format(charger.storage.getMaxEnergyStored())));
            list.add(I18n.format("gui.chargers.max_in", format.format(charger.storage.getMaxInput())));
            list.add(I18n.format("gui.chargers.max_out", format.format(charger.storage.getMaxOutput())));
            if (charger.getEnergyDiff() != 0) {
                list.add(I18n.format("gui.chargers.io", (charger.getEnergyDiff() > 0 ? TextFormatting.GREEN + "+": TextFormatting.RED.toString()) + charger.getEnergyDiff()));
            }
            drawHoveringText(list, mouseX, mouseY);
        }
    }
}
