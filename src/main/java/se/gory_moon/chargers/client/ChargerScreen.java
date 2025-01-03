package se.gory_moon.chargers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.EnergyFormatting;
import se.gory_moon.chargers.inventory.ChargerMenu;

import java.util.ArrayList;
import java.util.List;

public class ChargerScreen extends AbstractContainerScreen<ChargerMenu> {

    private static final ResourceLocation CHARGER_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/charger.png");

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
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);

        if (pMouseX >= leftPos + 44 && pMouseX <= leftPos + 44 + 16 && pMouseY >= topPos + 14 && pMouseY <= topPos + 84) {
            List<Component> list = new ArrayList<>();
            if (menu.isCreative())
                list.add(Component.translatable(LangKeys.POWER_INFO.key(), Component.translatable(LangKeys.ENERGY_INFINITE.key(), EnergyFormatting.FE).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GOLD));
            else
                list.add(EnergyFormatting.formatFilledCapacity(menu.getEnergy(), menu.getEnergyMax()));

            list.add(Component.translatable(LangKeys.GUI_MAX_IN.key(), EnergyFormatting.formatEnergyPerTick(menu.getMaxIn())).withStyle(ChatFormatting.GOLD));
            list.add(Component.translatable(LangKeys.GUI_MAX_OUT.key(), EnergyFormatting.formatEnergyPerTick(menu.getMaxOut())).withStyle(ChatFormatting.GOLD));

            if (menu.getAverageIn() > 0 || menu.getAverageOut() > 0) {
                list.add(Component.empty());
                if (hasShiftDown() || menu.getEnergyDiff() == 0) {
                    list.add(Component.translatable(LangKeys.GUI_DETAILS_IN.key(), EnergyFormatting.POSITIVE.copy().append(EnergyFormatting.formatEnergyPerTick(menu.getAverageIn()))).withStyle(ChatFormatting.GOLD));
                    list.add(Component.translatable(LangKeys.GUI_DETAILS_OUT.key(), EnergyFormatting.NEGATIVE.copy().append(EnergyFormatting.formatEnergyPerTick(menu.getAverageOut()))).withStyle(ChatFormatting.GOLD));
                } else {
                    if (menu.getEnergyDiff() != 0)
                        list.add(Component.translatable(LangKeys.GUI_IO.key(), (menu.getEnergyDiff() > 0 ? EnergyFormatting.POSITIVE : EnergyFormatting.NEGATIVE).copy().append(EnergyFormatting.formatEnergyPerTick(menu.getEnergyDiff()))).withStyle(ChatFormatting.GOLD));
                    list.add(Component.translatable(LangKeys.GUI_IO_MORE.key()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
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
    protected void renderLabels(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        super.renderLabels(graphics, pMouseX, pMouseY);
    }
}
