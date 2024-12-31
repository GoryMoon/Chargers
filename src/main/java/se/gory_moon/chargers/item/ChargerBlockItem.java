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
import se.gory_moon.chargers.block.ChargerBlock;
import se.gory_moon.chargers.block.ChargerBlock.Tier;

import java.util.List;

public class ChargerBlockItem extends BlockItem {

    public ChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Override
    public void onCraftedBy(ItemStack stack, @NotNull Level level, @NotNull Player player) {
        var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM, null);
        if (storage != null)
            storage.receiveEnergy(0, false);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        ChargerBlock block = (ChargerBlock) getBlock();
        Tier tier = block.getTier();
        if (tier.isCreative())
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFINITE_INFO.key()));
        else
            Utils.addEnergyTooltip(stack, tooltip);
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
