package se.gory_moon.chargers.crafting;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import se.gory_moon.chargers.Constants;

public class RecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<UpgradeChargerRecipeSerializer> UPGRADE_SERIALIZER = RECIPE_SERIALIZERS.register("upgrade_charger", UpgradeChargerRecipeSerializer::new);
}