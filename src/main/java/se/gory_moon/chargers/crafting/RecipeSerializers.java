package se.gory_moon.chargers.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.gory_moon.chargers.Constants;

public class RecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, UpgradeChargerRecipeSerializer> UPGRADE_SERIALIZER = RECIPE_SERIALIZER.register(
            "upgrade_charger",
            UpgradeChargerRecipeSerializer::new);
}
