package se.gory_moon.chargers.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;

import javax.annotation.Nullable;
import java.util.List;

public class WirelessChargerBlockItem extends BlockItem {

    public WirelessChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Override
    public void onCraftedBy(ItemStack stack, @NotNull Level level, @NotNull Player player) {
        var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage != null)
            storage.receiveEnergy(0, false);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Utils.addEnergyTooltip(stack, tooltip);
        tooltip.add(Component.translatable(LangKeys.TOOLTIP_WIRELESS_CHARGER.key()));
    }
}
