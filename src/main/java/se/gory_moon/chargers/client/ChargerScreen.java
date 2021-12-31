package se.gory_moon.chargers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.inventory.ChargerMenu;

import java.util.ArrayList;
import java.util.List;

public class ChargerScreen extends AbstractContainerScreen<ChargerMenu> {

    private static final ResourceLocation CHARGER_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID + ":textures/gui/charger.png");
    //private static final ResourceLocation CHARGER_BAUBLES_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID + ":textures/gui/charger_baubles.png");
    //public static final ResourceLocation CURIOS = new ResourceLocation("curios","textures/gui/inventory.png");

    public ChargerScreen(ChargerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageHeight = 172;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //boolean curios = Curios.isLoaded();
        /*if (curios) {
            minecraft.getTextureManager().bindTexture(CHARGER_BAUBLES_GUI_TEXTURE);
        } else {*/
            minecraft.getTextureManager().bind(CHARGER_GUI_TEXTURE);
        //}
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.hasEnergy()) {
            int progress = menu.getEnergyScaled(70);
            blit(matrixStack, leftPos + 48/* - (curios ? 9: 0)*/, topPos + 78 + 6 - progress, 0, 236 + 6 - progress, 16, progress);
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
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, title.getVisualOrderText(), imageWidth / 2f - this.font.width(title) / 2f, 4.0F, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
        //boolean curios = Curios.isLoaded();

        if (mouseX >= leftPos + 48/* - (curios ? 9: 0)*/ && mouseX <= leftPos + 48 + 16/* - (curios ? 9: 0) */&& mouseY >= topPos + 8 && mouseY <= topPos + 78) {
            List<ITextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent(LangKeys.GUI_ENERGY.key(), Utils.formatAndClean(menu.getEnergy()), Utils.formatAndClean(menu.getEnergyMax()) + TextFormatting.GRAY));
            list.add(new TranslationTextComponent(LangKeys.GUI_MAX_IN.key(), Utils.formatAndClean(menu.getMaxIn()) + TextFormatting.GRAY));
            list.add(new TranslationTextComponent(LangKeys.GUI_MAX_OUT.key(), Utils.formatAndClean(menu.getMaxOut()) + TextFormatting.GRAY));
            if (menu.getEnergyDiff() != 0) {
                list.add(new TranslationTextComponent(LangKeys.GUI_IO.key(), (menu.getEnergyDiff() > 0 ? TextFormatting.GREEN + "+": TextFormatting.RED.toString()) + Utils.formatAndClean(menu.getEnergyDiff()) + TextFormatting.GRAY));
            }
            renderComponentTooltip(matrixStack, list, mouseX, mouseY);
        }
        renderTooltip(matrixStack, mouseX, mouseY);
    }
}
