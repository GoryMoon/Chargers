package se.gory_moon.chargers.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.item.ChargerDataComponents;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
            dropEnergyBlock(BlockRegistry.CHARGER_BLOCK_T1);
            dropEnergyBlock(BlockRegistry.CHARGER_BLOCK_T2);
            dropEnergyBlock(BlockRegistry.CHARGER_BLOCK_T3);
            dropEnergyBlock(BlockRegistry.CHARGER_BLOCK_T4);
            dropEnergyBlock(BlockRegistry.CHARGER_BLOCK_CREATIVE);
            dropEnergyBlock(BlockRegistry.WIRELESS_CHARGER);
        }

        private void dropEnergyBlock(Supplier<? extends Block> blockSupplier) {
            var block = blockSupplier.get();
            add(block,
                    LootTable.lootTable().withPool(
                            applyExplosionCondition(
                                    block,
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0F))
                                            .add(LootItem.lootTableItem(block)
                                                    .apply(
                                                            CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                    .include(DataComponents.CUSTOM_NAME)
                                                                    .include(ChargerDataComponents.ENERGY.get())
                                                    )
                                            )
                            )
                    )
            );
        }
    }
}


