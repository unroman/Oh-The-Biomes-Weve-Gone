package net.potionstudios.biomeswevegone.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.biomeswevegone.tags.BWGBiomeTags;
import net.potionstudios.biomeswevegone.world.entity.BWGEntities;
import net.potionstudios.biomeswevegone.world.item.BWGItems;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import net.potionstudios.biomeswevegone.world.level.block.sand.BWGSandSet;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWoodSet;

import java.util.concurrent.CompletableFuture;

/**
 * DataGeneratorEntrypoint for Fabric
 * @see DataGeneratorEntrypoint
 * @author Joseph T. McQuigg
 */
public class FabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        FabricTagProvider.BlockTagProvider blockProvider = pack.addProvider(FabricBlockTagGenerator::new);
        pack.addProvider((output, completableFuture) -> new FabricItemTagGenerator(output, completableFuture, blockProvider));
        pack.addProvider(FabicEntityTagGenerator::new);
        pack.addProvider(FabricBiomeTagGenerator::new);
    }

    private static class FabricBlockTagGenerator extends FabricTagProvider.BlockTagProvider {

        public FabricBlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            BWGWoodSet.woodsets().forEach(set -> getOrCreateTagBuilder(ConventionalBlockTags.BOOKSHELVES).add(set.bookshelf()));
            BWGSandSet.getSandSets().forEach(set -> getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_BLOCKS).forceAddTag(set.getSandstoneBlocksTag()));
            getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES).add(BWGBlocks.FORAGERS_TABLE.get());
            //getOrCreateTagBuilder(ConventionalBlockTags.GLASS_BLOCKS).add(BWGBlocks.THERIUM_GLASS.get());
            //getOrCreateTagBuilder(ConventionalBlockTags.GLASS_PANES).add(BWGBlocks.THERIUM_GLASS_PANE.get());
        }
    }

    private static class FabricItemTagGenerator extends FabricTagProvider.ItemTagProvider {

        public FabricItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, FabricTagProvider.BlockTagProvider blockTags) {
            super(output, registriesFuture, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            copy(ConventionalBlockTags.BOOKSHELVES, ConventionalItemTags.BOOKSHELVES);
            copy(ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalItemTags.SANDSTONE_BLOCKS);
            copy(ConventionalBlockTags.VILLAGER_JOB_SITES, ConventionalItemTags.VILLAGER_JOB_SITES);
            //copy(ConventionalBlockTags.GLASS_BLOCKS, ConventionalItemTags.GLASS_BLOCKS);
            //copy(ConventionalBlockTags.GLASS_PANES, ConventionalItemTags.GLASS_PANES);
            BWGItems.ITEMS.stream()
                    .filter(item -> item.get().isEdible())
                    .forEach(item -> getOrCreateTagBuilder(ConventionalItemTags.FOODS).add(item.get()));

            getOrCreateTagBuilder(ConventionalItemTags.ENTITY_WATER_BUCKETS).add(BWGItems.MAN_O_WAR_BUCKET.get());
        }
    }

    private static class FabicEntityTagGenerator extends FabricTagProvider.EntityTypeTagProvider {

        public FabicEntityTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateTagBuilder(ConventionalEntityTypeTags.BOATS).add(BWGEntities.BWG_BOAT.get(), BWGEntities.BWG_CHEST_BOAT.get());
        }
    }

    private static class FabricBiomeTagGenerator extends FabricTagProvider<Biome> {

        /**
         * Constructs a new {@link FabricTagProvider} with the default computed path.
         *
         * <p>Common implementations of this class are provided.
         *
         * @param output           the {@link FabricDataOutput} instance
         * @param registriesFuture the backing registry for the tag type
         */
        public FabricBiomeTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.BIOME, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            /*
            getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_HOT).addTag(BWGBiomeTags.HOT_OVERWORLD);
            getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_WET).addTag(BWGBiomeTags.WET_OVERWORLD);
            getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_DRY).addTag(BWGBiomeTags.DRY_OVERWORLD);
            getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_COLD).addTag(BWGBiomeTags.COLD_OVERWORLD);
            getOrCreateTagBuilder(ConventionalBiomeTags.VEGETATION_SPARSE).addTag(BWGBiomeTags.SPARSE_OVERWORLD);
            getOrCreateTagBuilder(ConventionalBiomeTags.VEGETATION_DENSE).addTag(BWGBiomeTags.DENSE_OVERWORLD);

             */

            getOrCreateTagBuilder(ConventionalBiomeTags.PLAINS).addOptionalTag(BWGBiomeTags.PLAINS);
            getOrCreateTagBuilder(ConventionalBiomeTags.FOREST).addOptionalTag(BWGBiomeTags.FOREST);
            getOrCreateTagBuilder(ConventionalBiomeTags.TAIGA).addOptionalTag(BWGBiomeTags.TAIGA);
            getOrCreateTagBuilder(ConventionalBiomeTags.DESERT).addOptionalTag(BWGBiomeTags.DESERT);
            getOrCreateTagBuilder(ConventionalBiomeTags.SAVANNA).addOptionalTag(BWGBiomeTags.SAVANNA);
            getOrCreateTagBuilder(ConventionalBiomeTags.JUNGLE).addOptionalTag(BWGBiomeTags.JUNGLE);
            getOrCreateTagBuilder(ConventionalBiomeTags.BEACH).addOptionalTag(BWGBiomeTags.BEACH);
            getOrCreateTagBuilder(ConventionalBiomeTags.OCEAN).addOptionalTag(BWGBiomeTags.OCEAN);
            getOrCreateTagBuilder(ConventionalBiomeTags.SNOWY).addOptionalTag(BWGBiomeTags.SNOWY);
            getOrCreateTagBuilder(ConventionalBiomeTags.MOUNTAIN).addOptionalTag(BWGBiomeTags.MOUNTAIN);
            getOrCreateTagBuilder(ConventionalBiomeTags.BADLANDS).addOptionalTag(BWGBiomeTags.BADLANDS);
            getOrCreateTagBuilder(ConventionalBiomeTags.SWAMP).addOptionalTag(BWGBiomeTags.SWAMP);
            getOrCreateTagBuilder(ConventionalBiomeTags.DEAD).addOptionalTag(BWGBiomeTags.DEAD);
            //getOrCreateTagBuilder(ConventionalBiomeTags.CAVES).addTag(BWGBiomeTags.CAVE);
        }
    }
}
