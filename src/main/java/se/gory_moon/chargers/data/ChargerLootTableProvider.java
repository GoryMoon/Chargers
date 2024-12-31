package se.gory_moon.chargers.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.block.BlockRegistry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChargerLootTableProvider extends LootTableProvider {

    public ChargerLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(
                        ChargerBlockLootSubProvider::new,
                        LootContextParamSets.BLOCK
                )
        ), registries);
    }

    private static class ChargerBlockLootSubProvider extends BlockLootSubProvider {

        private ChargerBlockLootSubProvider(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return BlockRegistry.BLOCKS.getEntries()
                    .stream()
                    .map(holder -> (Block) holder.value())
                    .toList();
        }

        @Override
        protected void generate() {
            dropSelf(BlockRegistry.CHARGER_BLOCK_T1.get());
            dropSelf(BlockRegistry.CHARGER_BLOCK_T2.get());
            dropSelf(BlockRegistry.CHARGER_BLOCK_T3.get());
            dropSelf(BlockRegistry.CHARGER_BLOCK_T4.get());
            dropSelf(BlockRegistry.CHARGER_BLOCK_CREATIVE.get());
            dropSelf(BlockRegistry.WIRELESS_CHARGER.get());
        }
    }
}


