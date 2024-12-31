package se.gory_moon.chargers.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;

public class UpgradeChargerRecipeSerializer implements RecipeSerializer<UpgradeChargerRecipe> {
    private final MapCodec<UpgradeChargerRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
                Codec.BOOL.optionalFieldOf("show_notification", Boolean.TRUE).forGetter(ShapedRecipe::showNotification)
        ).apply(instance, UpgradeChargerRecipe::new)
    );

    private final StreamCodec<RegistryFriendlyByteBuf, UpgradeChargerRecipe> STREAM_CODEC = StreamCodec.of(
            UpgradeChargerRecipeSerializer::toNetwork, UpgradeChargerRecipeSerializer::fromNetwork
    );

    @Override
    public @NotNull MapCodec<UpgradeChargerRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, UpgradeChargerRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static UpgradeChargerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        ShapedRecipe basic = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
        return new UpgradeChargerRecipe(basic.getGroup(), basic.category(), basic.pattern, basic.getResultItem(buffer.registryAccess()), basic.showNotification());
    }

    public static void toNetwork(@NotNull RegistryFriendlyByteBuf buffer, UpgradeChargerRecipe recipe) {
        RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe.toVanilla(buffer.registryAccess()));
    }
}
