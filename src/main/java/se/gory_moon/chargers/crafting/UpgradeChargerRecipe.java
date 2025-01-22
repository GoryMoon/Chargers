package se.gory_moon.chargers.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.block.EnergyBlock;
import se.gory_moon.chargers.power.CustomEnergyStorage;

import java.util.concurrent.atomic.AtomicLong;

public class UpgradeChargerRecipe extends ShapedRecipe {
    public UpgradeChargerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack output, boolean showNotification) {
        super(group, category, pattern, output, showNotification);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.UPGRADE_SERIALIZER.get();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        var out = getResultItem(registries).copy();

        if (out.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof EnergyBlock) {
            AtomicLong energy = new AtomicLong();

            for (int i = 0; i < input.size(); i++) {
                ItemStack item = input.getItem(i);
                if (!item.isEmpty() && item.getItem() instanceof BlockItem blockItem1 && blockItem1.getBlock() instanceof EnergyBlock) {
                    var storage = item.getCapability(Capabilities.EnergyStorage.ITEM);
                    if (storage != null) {
                        if (storage instanceof CustomEnergyStorage customEnergyStorage)
                            energy.addAndGet(customEnergyStorage.getLongEnergyStored());
                        else
                            energy.addAndGet(storage.getEnergyStored());
                    }
                }
            }

            var outStorage = out.getCapability(Capabilities.EnergyStorage.ITEM);
            if (outStorage instanceof CustomEnergyStorage customEnergyStorage) {
                customEnergyStorage.setEnergy(Math.min(energy.get(), customEnergyStorage.getLongMaxEnergyStored()));
            }
            return out;
        }
        return super.assemble(input, registries);
    }

    public ShapedRecipe toVanilla(HolderLookup.Provider registries) {
        return new ShapedRecipe(getGroup(), category(), pattern, getResultItem(registries));
    }
}
