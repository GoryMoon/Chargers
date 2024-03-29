package se.gory_moon.chargers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
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
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);

        if (pMouseX >= leftPos + 44 && pMouseX <= leftPos + 44 + 16 && pMouseY >= topPos + 14 && pMouseY <= topPos + 84) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable(LangKeys.GUI_ENERGY.key(), Utils.formatAndClean(menu.getEnergy()), Utils.formatAndClean(menu.getEnergyMax()) + ChatFormatting.GRAY));
            list.add(Component.translatable(LangKeys.GUI_MAX_IN.key(), Utils.formatAndClean(menu.getMaxIn()) + ChatFormatting.GRAY));
            list.add(Component.translatable(LangKeys.GUI_MAX_OUT.key(), Utils.formatAndClean(menu.getMaxOut()) + ChatFormatting.GRAY));
            if (menu.getEnergyDiff() != 0) {
                list.add(Component.translatable(LangKeys.GUI_IO.key(), (menu.getEnergyDiff() > 0 ? ChatFormatting.GREEN + "+": ChatFormatting.RED.toString()) + Utils.formatAndClean(menu.getEnergyDiff()) + ChatFormatting.GRAY));
            }
            graphics.renderComponentTooltip(this.font, list, pMouseX, pMouseY);
        }
        renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.blit(CHARGER_GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.hasEnergy()) {
            int progress = menu.getEnergyScaled(70);
            graphics.blit(CHARGER_GUI_TEXTURE, leftPos + 44, topPos + 78 + 6 - progress, 176, 70 - progress, 16, progress);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
        super.renderLabels(graphics, pMouseX, pMouseY);
    }
}
