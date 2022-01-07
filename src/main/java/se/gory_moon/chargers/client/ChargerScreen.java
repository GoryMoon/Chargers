package se.gory_moon.chargers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.inventory.ChargerMenu;

import java.util.ArrayList;
import java.util.List;

public class ChargerScreen extends AbstractContainerScreen<ChargerMenu> {

    private static final ResourceLocation CHARGER_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/charger.png");

    public ChargerScreen(ChargerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageHeight = 199;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 4;
        this.inventoryLabelY = 107;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (pMouseX >= leftPos + 44 && pMouseX <= leftPos + 44 + 16 && pMouseY >= topPos + 14 && pMouseY <= topPos + 84) {
            List<Component> list = new ArrayList<>();
            list.add(new TranslatableComponent(LangKeys.GUI_ENERGY.key(), Utils.formatAndClean(menu.getEnergy()), Utils.formatAndClean(menu.getEnergyMax()) + ChatFormatting.GRAY));
            list.add(new TranslatableComponent(LangKeys.GUI_MAX_IN.key(), Utils.formatAndClean(menu.getMaxIn()) + ChatFormatting.GRAY));
            list.add(new TranslatableComponent(LangKeys.GUI_MAX_OUT.key(), Utils.formatAndClean(menu.getMaxOut()) + ChatFormatting.GRAY));
            if (menu.getEnergyDiff() != 0) {
                list.add(new TranslatableComponent(LangKeys.GUI_IO.key(), (menu.getEnergyDiff() > 0 ? ChatFormatting.GREEN + "+": ChatFormatting.RED.toString()) + Utils.formatAndClean(menu.getEnergyDiff()) + ChatFormatting.GRAY));
            }
            renderComponentTooltip(pPoseStack, list, pMouseX, pMouseY);
        }
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, CHARGER_GUI_TEXTURE);

        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.hasEnergy()) {
            int progress = menu.getEnergyScaled(70);
            blit(pPoseStack, leftPos + 44, topPos + 78 + 6 - progress, 176, 70 - progress, 16, progress);
        }
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
    }
}
