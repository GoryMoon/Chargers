package se.gory_moon.chargers.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.item.ChargerBlockItem;

import java.util.concurrent.atomic.AtomicInteger;

public class UpgradeChargerRecipe extends ShapedRecipe {
    public UpgradeChargerRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output) {
        super(id, group, category, width, height, ingredients, output);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.UPGRADE_SERIALIZER.get();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess access) {
        var out = getResultItem(access).copy();

        if (out.getItem() instanceof ChargerBlockItem) {
            AtomicInteger energy = new AtomicInteger();

            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (!item.isEmpty() && item.getItem() instanceof ChargerBlockItem)
                    item.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(storage -> energy.addAndGet(storage.getEnergyStored()));
            }

            out.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(storage -> {
                out.getOrCreateTag().putInt("Energy", Math.min(energy.get(), storage.getMaxEnergyStored()));
            });
            return out;
        }
        return super.assemble(inv, access);
    }

    public ShapedRecipe toVanilla() {
        return new ShapedRecipe(getId(), getGroup(), category(), getWidth(), getHeight(), getIngredients(), getResultItem(null));
    }
}
