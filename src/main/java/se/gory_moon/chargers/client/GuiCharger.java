package se.gory_moon.chargers.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.IItemHandler;
import se.gory_moon.chargers.compat.Baubles;
import se.gory_moon.chargers.inventory.ContainerCharger;
import se.gory_moon.chargers.lib.ModInfo;
import se.gory_moon.chargers.tile.TileEntityCharger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiCharger extends GuiContainer {

    private TileEntityCharger charger;
    private static final ResourceLocation CHARGER_GUI_TEXTURE = new ResourceLocation(ModInfo.MODID + ":textures/gui/charger.png");
    private static final ResourceLocation CHARGER_BAUBLES_GUI_TEXTURE = new ResourceLocation(ModInfo.MODID + ":textures/gui/charger_baubles.png");
    public static final ResourceLocation BAUBLES = new ResourceLocation("baubles","textures/gui/expanded_inventory.png");
    public GuiCharger(EntityPlayer player, TileEntityCharger tile) {
        super(new ContainerCharger(player, tile));
        this.charger = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        boolean baubles = Baubles.isLoaded();
        if (baubles) {
            mc.renderEngine.bindTexture(CHARGER_BAUBLES_GUI_TEXTURE);
        } else {
            mc.renderEngine.bindTexture(CHARGER_GUI_TEXTURE);
        }
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (charger.storage.getEnergyStored() > 0) {
            double progress = ((double)charger.storage.getEnergyStored() / (double) charger.storage.getMaxEnergyStored() * 70);
            drawTexturedModalRect(guiLeft + 48 - (baubles ? 9: 0), guiTop + 78 - progress, 0, 236 - progress, 16, progress);
        }

        if (baubles) {
            mc.renderEngine.bindTexture(BAUBLES);
            IItemHandler handler = ((ContainerCharger)inventorySlots).baubles;
            for (int i = 0; i < 7; i++) {
                if (handler.getStackInSlot(i).isEmpty()) {
                    int texX = 77 + (i / 4) * 19;
                    int texY = 8 + (i % 4) * 18;
                    drawTexturedModalRect(guiLeft + 103 + (i / 4) * 18, guiTop + 8 + (i % 4) * 18, texX, texY, 16, 16);
                }
            }
        }
    }

    private void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width, double height)
    {
        double f = 0.00390625D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, (double)this.zLevel)           .tex(textureX * f, (textureY + height) * f)             .endVertex();
        bufferbuilder.pos(x + width, y + height, (double)this.zLevel)   .tex((textureX + width) * f, (textureY + height) * f)   .endVertex();
        bufferbuilder.pos(x + width, y, (double)this.zLevel)            .tex((textureX + width) * f, textureY * f)              .endVertex();
        bufferbuilder.pos(x, y, (double)this.zLevel)                    .tex(textureX * f, textureY * f)                        .endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        boolean baubles = Baubles.isLoaded();

        if (mouseX >= guiLeft + 48 - (baubles ? 9: 0) && mouseX <= guiLeft + 48 + 16 - (baubles ? 9: 0) && mouseY >= guiTop + 8 && mouseY <= guiTop + 78) {
            NumberFormat format = NumberFormat.getInstance();
            List<String> list = new ArrayList<>();
            list.add(I18n.format("gui.chargers.energy", format.format(charger.storage.getEnergyStored()), format.format(charger.storage.getMaxEnergyStored()) + TextFormatting.GRAY));
            list.add(I18n.format("gui.chargers.max_in", format.format(charger.storage.getMaxInput()) + TextFormatting.GRAY));
            list.add(I18n.format("gui.chargers.max_out", format.format(charger.storage.getMaxOutput()) + TextFormatting.GRAY));
            if (charger.getEnergyDiff() != 0) {
                list.add(I18n.format("gui.chargers.io", (charger.getEnergyDiff() > 0 ? TextFormatting.GREEN + "+": TextFormatting.RED.toString()) + Math.round(charger.getEnergyDiff()) + TextFormatting.GRAY));
            }
            drawHoveringText(list, mouseX, mouseY);
        }
    }
}
