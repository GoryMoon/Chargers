package se.gory_moon.chargers.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.crafting.UpgradeChargerRecipeBuilder;
import se.gory_moon.chargers.item.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class ChargerRecipeProvider extends RecipeProvider {
    public ChargerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        var chargerT1Item = ItemRegistry.CHARGER_T1_ITEM.get();
        var chargerT2Item = ItemRegistry.CHARGER_T2_ITEM.get();
        var chargerT3Item = ItemRegistry.CHARGER_T3_ITEM.get();
        var chargerT4Item = ItemRegistry.CHARGER_T4_ITEM.get();

        var iron = Ingredient.of(Tags.Items.INGOTS_IRON);
        var redstone = Ingredient.of(Tags.Items.DUSTS_REDSTONE);
        var redstoneBlock = Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE);
        var gold = Ingredient.of(Tags.Items.INGOTS_GOLD);
        var diamond = Ingredient.of(Tags.Items.GEMS_DIAMOND);
        var netherite = Ingredient.of(Tags.Items.INGOTS_NETHERITE);
        var enderPearls = Ingredient.of(Tags.Items.ENDER_PEARLS);

        var chargerT1 = Ingredient.of(chargerT1Item);
        var chargerT2 = Ingredient.of(chargerT2Item);
        var chargerT3 = Ingredient.of(chargerT3Item);

        // T1 Charger
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, chargerT1Item)
                .define('I', iron)
                .define('R', redstone)
                .define('B', redstoneBlock)
                .pattern("IRI").pattern("IBI").pattern("IRI")
                .unlockedBy("has_ingots_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_dusts_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_storage_blocks_redstone", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .save(output);

        // T2 Charger
        new UpgradeChargerRecipeBuilder(RecipeCategory.REDSTONE, chargerT2Item)
                .define('G', gold)
                .define('R', redstone)
                .define('B', redstoneBlock)
                .define('C', chargerT1)
                .pattern("GRG").pattern("GBG").pattern("GCG")
                .unlockedBy("has_charger_t1", has(chargerT1Item))
                .save(output);

        // T3 Charger
        new UpgradeChargerRecipeBuilder(RecipeCategory.REDSTONE, chargerT3Item)
                .define('D', diamond)
                .define('R', redstone)
                .define('B', redstoneBlock)
                .define('C', chargerT2)
                .pattern("DRD").pattern("DBD").pattern("DCD")
                .unlockedBy("has_charger_t2", has(chargerT2Item))
                .save(output);

        // T4 Charger
        new UpgradeChargerRecipeBuilder(RecipeCategory.REDSTONE, chargerT4Item)
                .define('N', netherite)
                .define('R', redstone)
                .define('B', redstoneBlock)
                .define('C', chargerT3)
                .pattern("NRN").pattern("NBN").pattern("NCN")
                .unlockedBy("has_charger_t3", has(chargerT3Item))
                .save(output);

        // Wireless charger
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.WIRELESS_CHARGER.get())
                .define('I', iron)
                .define('R', redstone)
                .define('B', redstoneBlock)
                .define('E', enderPearls)
                .pattern("IEI").pattern("IBI").pattern("IRI")
                .unlockedBy("has_ender_pearls", has(Tags.Items.ENDER_PEARLS))
                .unlockedBy("has_ingots_iron", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_dusts_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_storage_blocks_redstone", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .save(output);
    }
}
