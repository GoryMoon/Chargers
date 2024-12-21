package se.gory_moon.chargers.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.LangKeys;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.block.ChargerBlock.Tier;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class ChargerBlockItem extends BlockItem {

    public ChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        Tier tier = Tier.byItem(this);
        return new ItemEnergyCapabilityProvider(new CustomItemEnergyStorage(stack, tier.getStorage(), tier.getMaxIn(), tier.getMaxOut(), tier.isCreative()));
    }

    @Override
    public void onCraftedBy(ItemStack stack, @NotNull Level level, @NotNull Player player) {
        stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(0, false));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        Tier tier = Tier.byItem(this);
        if (tier.isCreative())
            tooltip.add(Component.translatable(LangKeys.CHAT_STORED_INFINITE_INFO.key()));
        else
            Utils.addEnergyTooltip(stack, tooltip);
        super.appendHoverText(stack, level, tooltip, flagIn);
    }
}
