package se.gory_moon.chargers.crafting;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class UpgradeChargerRecipeBuilder extends ShapedRecipeBuilder {
    public UpgradeChargerRecipeBuilder(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        super(pCategory, pResult, pCount);
    }

    public static UpgradeChargerRecipeBuilder builder(RecipeCategory category, ItemLike result) {
        return builder(category, result, 1);
    }

    public static UpgradeChargerRecipeBuilder builder(RecipeCategory category, ItemLike result, int count) {
        return new UpgradeChargerRecipeBuilder(category, result, count);
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer, @NotNull ResourceLocation recipeId) {
        Consumer<FinishedRecipe> dummyConsumer = recipe -> finishedRecipeConsumer.accept(new Result(recipe));
        super.save(dummyConsumer, recipeId);
    }

    public static class Result implements FinishedRecipe {
        private final FinishedRecipe recipe;

        public Result(FinishedRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            recipe.serializeRecipeData(json);
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return RecipeSerializers.UPGRADE_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return recipe.serializeAdvancement();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return recipe.getAdvancementId();
        }
    }
}
