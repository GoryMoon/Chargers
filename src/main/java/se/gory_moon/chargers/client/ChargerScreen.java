package se.gory_moon.chargers.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
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
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //boolean curios = Curios.isLoaded();
        /*if (curios) {
            minecraft.getTextureManager().bindTexture(CHARGER_BAUBLES_GUI_TEXTURE);
        } else {*/
            minecraft.getTextureManager().bindTexture(CHARGER_GUI_TEXTURE);
        //}
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);

        if (container.hasEnergy()) {
            int progress = container.getEnergyScaled(70);
            blit(matrixStack, guiLeft + 48/* - (curios ? 9: 0)*/, guiTop + 78 + 6 - progress, 0, 236 + 6 - progress, 16, progress);
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
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.func_238422_b_(matrixStack, title.func_241878_f(), xSize / 2f - this.font.getStringPropertyWidth(title) / 2f, 4.0F, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
        //boolean curios = Curios.isLoaded();

        if (mouseX >= guiLeft + 48/* - (curios ? 9: 0)*/ && mouseX <= guiLeft + 48 + 16/* - (curios ? 9: 0) */&& mouseY >= guiTop + 8 && mouseY <= guiTop + 78) {
            NumberFormat format = NumberFormat.getInstance();
            List<ITextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent(LangKeys.GUI_ENERGY.key(), Utils.clean(format.format(container.getEnergy())), Utils.clean(format.format(container.getEnergyMax())) + TextFormatting.GRAY));
            list.add(new TranslationTextComponent(LangKeys.GUI_MAX_IN.key(), Utils.clean(format.format(container.getMaxIn())) + TextFormatting.GRAY));
            list.add(new TranslationTextComponent(LangKeys.GUI_MAX_OUT.key(), Utils.clean(format.format(container.getMaxOut())) + TextFormatting.GRAY));
            if (container.getEnergyDiff() != 0) {
                list.add(new TranslationTextComponent(LangKeys.GUI_IO.key(), (container.getEnergyDiff() > 0 ? TextFormatting.GREEN + "+": TextFormatting.RED.toString()) + Utils.clean(format.format(container.getEnergyDiff())) + TextFormatting.GRAY));
            }
            func_243308_b(matrixStack, list, mouseX, mouseY);
        }
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
}
