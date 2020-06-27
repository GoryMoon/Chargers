package se.gory_moon.chargers.blocks;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import se.gory_moon.chargers.ChargersMod;
import se.gory_moon.chargers.items.ChargerBlockItem;
import se.gory_moon.chargers.items.ItemRegistry;
import se.gory_moon.chargers.items.WirelessChargerBlockItem;
import se.gory_moon.chargers.tile.WirelessChargerTileEntity;

import static se.gory_moon.chargers.Constants.*;

public class BlockRegistry {
    private static final Registrate REGISTRATE = ChargersMod.getRegistrate();

    public static BlockEntry<ChargerBlock> CHARGER_BLOCK_T1 = REGISTRATE.object(CHARGER_T1_BLOCK)
            .block(ChargerBlock::new)
            .initialProperties(Material.IRON, MaterialColor.GRAY)
            .lang(CHARGER_T1_NAME)
            .properties(properties -> properties.hardnessAndResistance(5, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))
            .blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(),
                    provider.models().cubeBottomTop("charger_tier_1",
                            provider.modLoc("block/charger_tier_1_side"),
                            provider.modLoc("block/charger_tier_1"),
                            provider.modLoc("block/charger_tier_1_top"))))
            .item(ChargerBlockItem::new)
                .properties(p -> p.rarity(Rarity.COMMON).group(ItemGroup.REDSTONE))
                .recipe((context, provider) -> {
                    DataIngredient iron = DataIngredient.tag(Tags.Items.INGOTS_IRON);
                    DataIngredient redstone = DataIngredient.tag(Tags.Items.DUSTS_REDSTONE);
                    DataIngredient redstoneBlock = DataIngredient.tag(Tags.Items.STORAGE_BLOCKS_REDSTONE);

                    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(context.get())
                            .key('I', iron)
                            .key('R', redstone)
                            .key('B', redstoneBlock);
                    builder.patternLine("IRI").patternLine("IBI").patternLine("IRI");
                    builder.addCriterion("has_" + provider.safeName(iron), iron.getCritereon(provider))
                            .addCriterion("has_" + provider.safeName(redstone), redstone.getCritereon(provider))
                            .addCriterion("has_" + provider.safeName(redstoneBlock), redstoneBlock.getCritereon(provider));
                    builder.build(provider, provider.safeId(context.get()));
                })
                .build()
            .register();

    public static BlockEntry<ChargerBlock> CHARGER_BLOCK_T2 = REGISTRATE.object(CHARGER_T2_BLOCK)
            .block(ChargerBlock::new)
            .initialProperties(Material.IRON, MaterialColor.GOLD)
            .lang(CHARGER_T2_NAME)
            .properties(properties -> properties.hardnessAndResistance(5, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))
            .blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(),
                    provider.models().cubeBottomTop("charger_tier_2",
                            provider.modLoc("block/charger_tier_2_side"),
                            provider.modLoc("block/charger_tier_2"),
                            provider.modLoc("block/charger_tier_2_top"))))
            .item(ChargerBlockItem::new)
                .properties(p -> p.rarity(Rarity.UNCOMMON).group(ItemGroup.REDSTONE))
                .recipe((context, provider) -> {
                    DataIngredient gold = DataIngredient.tag(Tags.Items.INGOTS_GOLD);
                    DataIngredient redstone = DataIngredient.tag(Tags.Items.DUSTS_REDSTONE);
                    DataIngredient redstoneBlock = DataIngredient.tag(Tags.Items.STORAGE_BLOCKS_REDSTONE);
                    DataIngredient charger_t1 = DataIngredient.items(ItemRegistry.CHARGER_T1_ITEM.get());

                    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(context.get())
                            .key('G', gold)
                            .key('R', redstone)
                            .key('B', redstoneBlock)
                            .key('C', charger_t1);
                    builder.patternLine("GRG").patternLine("GBG").patternLine("GCG");
                    builder.addCriterion("has_" + provider.safeName(charger_t1), charger_t1.getCritereon(provider));
                    builder.build(provider, provider.safeId(context.get()));
                })
                .build()
            .register();

    public static BlockEntry<ChargerBlock> CHARGER_BLOCK_T3 = REGISTRATE.object(CHARGER_T3_BLOCK)
            .block(ChargerBlock::new)
            .initialProperties(Material.IRON, MaterialColor.CYAN)
            .lang(CHARGER_T3_NAME)
            .properties(properties -> properties.hardnessAndResistance(5, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))
            .blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(),
                    provider.models().cubeBottomTop("charger_tier_3",
                            provider.modLoc("block/charger_tier_3_side"),
                            provider.modLoc("block/charger_tier_3"),
                            provider.modLoc("block/charger_tier_3_top"))))
            .item(ChargerBlockItem::new)
                .properties(p -> p.rarity(Rarity.RARE).group(ItemGroup.REDSTONE))
                .recipe((context, provider) -> {
                    DataIngredient diamond = DataIngredient.tag(Tags.Items.GEMS_DIAMOND);
                    DataIngredient redstone = DataIngredient.tag(Tags.Items.DUSTS_REDSTONE);
                    DataIngredient redstoneBlock = DataIngredient.tag(Tags.Items.STORAGE_BLOCKS_REDSTONE);
                    DataIngredient charger_t2 = DataIngredient.items(ItemRegistry.CHARGER_T2_ITEM.get());

                    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(context.get())
                            .key('D', diamond)
                            .key('R', redstone)
                            .key('B', redstoneBlock)
                            .key('C', charger_t2);
                    builder.patternLine("DRD").patternLine("DBD").patternLine("DCD");
                    builder.addCriterion("has_" + provider.safeName(charger_t2), charger_t2.getCritereon(provider));
                    builder.build(provider, provider.safeId(context.get()));
                })
                .build()
            .register();

    public static final BlockEntry<WirelessChargerBlock> WIRELESS_CHARGER = REGISTRATE.object(WIRELESS_CHARGER_BLOCK)
            .block(WirelessChargerBlock::new)
            .initialProperties(Material.IRON, MaterialColor.GRAY)
            .simpleTileEntity(WirelessChargerTileEntity::new)
            .lang(WIRELESS_CHARGER_NAME)
            .properties(properties -> properties.hardnessAndResistance(5, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0))
            .blockstate((ctx, provider) -> provider.getVariantBuilder(ctx.get())
                    .partialState().with(WirelessChargerBlock.POWERED, false)
                        .modelForState().modelFile(provider.models().cubeAll("wireless_charger_disabled", provider.modLoc("block/wireless_charger_disabled"))).addModel()
                    .partialState().with(WirelessChargerBlock.POWERED, true)
                        .modelForState().modelFile(provider.models().cubeAll("wireless_charger_enabled", provider.modLoc("block/wireless_charger_enabled"))).addModel()
            )
            .item(WirelessChargerBlockItem::new)
                .properties(properties -> properties.group(ItemGroup.REDSTONE))
                .model((context, provider) -> provider.blockItem(() -> context.get().getBlock(), "_disabled"))
                .recipe((context, provider) -> {
                    DataIngredient iron = DataIngredient.tag(Tags.Items.INGOTS_IRON);
                    DataIngredient redstone = DataIngredient.tag(Tags.Items.DUSTS_REDSTONE);
                    DataIngredient redstoneBlock = DataIngredient.tag(Tags.Items.STORAGE_BLOCKS_REDSTONE);
                    DataIngredient enderPearls = DataIngredient.tag(Tags.Items.ENDER_PEARLS);

                    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(context.get())
                            .key('I', iron)
                            .key('R', redstone)
                            .key('B', redstoneBlock)
                            .key('E', enderPearls);
                    builder.patternLine("IEI").patternLine("IBI").patternLine("IRI");
                    builder.addCriterion("has_" + provider.safeName(enderPearls), enderPearls.getCritereon(provider))
                            .addCriterion("has_" + provider.safeName(iron), iron.getCritereon(provider))
                            .addCriterion("has_" + provider.safeName(redstone), redstone.getCritereon(provider))
                            .addCriterion("has_" + provider.safeName(redstoneBlock), redstoneBlock.getCritereon(provider));
                    builder.build(provider, provider.safeId(context.get()));
                })
                .build()
            .register();


    private BlockRegistry() {}

    public static void init() {}
}
