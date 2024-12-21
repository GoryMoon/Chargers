package se.gory_moon.chargers.crafting;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeChargerRecipeSerializer implements RecipeSerializer<UpgradeChargerRecipe> {

    @Override
    public MapCodec<UpgradeChargerRecipe> codec() {
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, UpgradeChargerRecipe> streamCodec() {
        return null;
    }

    @Override
    public @NotNull UpgradeChargerRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        ShapedRecipe basic = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
        return new UpgradeChargerRecipe(basic.getId(), basic.getGroup(), basic.category(), basic.getWidth(), basic.getHeight(), basic.getIngredients(), basic.getResultItem(null));
    }

    @Override
    public @Nullable UpgradeChargerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
        ShapedRecipe basic = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
        return basic != null ? new UpgradeChargerRecipe(basic.getId(), basic.getGroup(), basic.category(), basic.getWidth(), basic.getHeight(), basic.getIngredients(), basic.getResultItem(null)) : null;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, UpgradeChargerRecipe recipe) {
        RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.toVanilla());
    }
}
