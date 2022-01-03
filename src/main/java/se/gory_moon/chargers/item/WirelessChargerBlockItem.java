package se.gory_moon.chargers.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
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
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(0, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Utils.addEnergyTooltip(stack, tooltip);
        tooltip.add(new TranslatableComponent(getDescriptionId() + ".desc"));
    }
}
