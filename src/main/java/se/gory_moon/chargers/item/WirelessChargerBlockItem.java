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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Configs;
import se.gory_moon.chargers.Utils;
import se.gory_moon.chargers.power.CustomItemEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class WirelessChargerBlockItem extends BlockItem {

    public WirelessChargerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemEnergyCapabilityProvider(new CustomItemEnergyStorage(stack,
                Configs.SERVER.wireless.storage.get(),
                Configs.SERVER.wireless.maxInput.get(),
                Configs.SERVER.wireless.maxOutput.get()));
    }

    @Override
    public void onCraftedBy(ItemStack stack, @NotNull Level level, @NotNull Player player) {
        stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(0, false));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Utils.addEnergyTooltip(stack, tooltip);
        tooltip.add(Component.translatable(getDescriptionId() + ".desc"));
    }
}
