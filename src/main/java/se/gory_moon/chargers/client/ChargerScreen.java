package se.gory_moon.chargers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.inventory.ContainerCharger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ChargerScreen extends ContainerScreen<ContainerCharger> {

    private static final ResourceLocation CHARGER_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID + ":textures/gui/charger.png");
    //private static final ResourceLocation CHARGER_BAUBLES_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID + ":textures/gui/charger_baubles.png");
    //public static final ResourceLocation CURIOS = new ResourceLocation("curios","textures/gui/inventory.png");

    public ChargerScreen(ContainerCharger container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        ySize = 172;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //boolean curios = Curios.isLoaded();
        /*if (curios) {
            minecraft.getTextureManager().bindTexture(CHARGER_BAUBLES_GUI_TEXTURE);
        } else {*/
            minecraft.getTextureManager().bindTexture(CHARGER_GUI_TEXTURE);
        //}
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (container.hasEnergy()) {
            int progress = container.getEnergyScaled(70);
            blit(guiLeft + 48/* - (curios ? 9: 0)*/, guiTop + 78 + 6 - progress, 0, 236 + 6 - progress, 16, progress);
        }

        /*if (curios) {
            minecraft.getTextureManager().bindTexture(CURIOS);
            IItemHandler handler = container.curios;
            if (handler != null) {
                for (int i = 0; i < 7; i++) {
                    if (handler.getStackInSlot(i).isEmpty()) {
                        int texX = 77 + (i / 4) * 19;
                        int texY = 8 + (i % 4) * 18;
                        blit(guiLeft + 103 + (i / 4) * 18, guiTop + 14 + (i % 4) * 18, texX, texY, 16, 16);
                    }
                }
            }
        }*/
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = this.title.getFormattedText();
        this.font.drawString(title, xSize / 2f - this.font.getStringWidth(title) / 2f, 4.0F, 4210752);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        //boolean curios = Curios.isLoaded();

        if (mouseX >= guiLeft + 48/* - (curios ? 9: 0)*/ && mouseX <= guiLeft + 48 + 16/* - (curios ? 9: 0) */&& mouseY >= guiTop + 8 && mouseY <= guiTop + 78) {
            NumberFormat format = NumberFormat.getInstance();
            List<String> list = new ArrayList<>();
            list.add(I18n.format("gui.chargers.energy", format.format(container.getEnergy()), format.format(container.getEnergyMax()) + TextFormatting.GRAY));
            list.add(I18n.format("gui.chargers.max_in", format.format(container.getMaxIn()) + TextFormatting.GRAY));
            list.add(I18n.format("gui.chargers.max_out", format.format(container.getMaxOut()) + TextFormatting.GRAY));
            if (container.getEnergyDiff() != 0) {
                list.add(I18n.format("gui.chargers.io", (container.getEnergyDiff() > 0 ? TextFormatting.GREEN + "+": TextFormatting.RED.toString()) + Math.round(container.getEnergyDiff()) + TextFormatting.GRAY));
            }
            renderTooltip(list, mouseX, mouseY);
        }
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
