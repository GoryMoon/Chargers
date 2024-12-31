package se.gory_moon.chargers.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class ChargerBlockTagsProvider extends BlockTagsProvider {

    public ChargerBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        BlockRegistry.CHARGER_BLOCK_T1.get(),
                        BlockRegistry.CHARGER_BLOCK_T2.get(),
                        BlockRegistry.CHARGER_BLOCK_T3.get(),
                        BlockRegistry.CHARGER_BLOCK_T4.get(),
                        BlockRegistry.CHARGER_BLOCK_CREATIVE.get(),
                        BlockRegistry.WIRELESS_CHARGER.get()
                );

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(BlockRegistry.CHARGER_BLOCK_T2.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(BlockRegistry.CHARGER_BLOCK_T3.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        BlockRegistry.CHARGER_BLOCK_T4.get(),
                        BlockRegistry.CHARGER_BLOCK_CREATIVE.get()
                );
    }
}
