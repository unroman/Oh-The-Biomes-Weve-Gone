package net.potionstudios.biomeswevegone.world.level.block;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.potionstudios.biomeswevegone.world.item.BWGItems;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWood;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWoodSet;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

/**
 * Block Features that need to be added separately from registration
 * @author Joseph T. McQuigg
 */
public class BlockFeatures {

    public static void registerCompostables(BiConsumer<ItemLike, Float> consumer) {
        BWGBlocks.BLOCKS.forEach(block -> {
            if (block.get() instanceof LeavesBlock || block.get() instanceof TallGrassBlock)
                consumer.accept(block.get(), 0.3F);
            else if (block.get() instanceof FlowerBlock || block.get() instanceof TallFlowerBlock || block.get() instanceof WaterlilyBlock || block.get() instanceof MushroomBlock)
                consumer.accept(block.get(), 0.65F);
        });

        BWGWood.WOOD.forEach(entry -> {
            if (entry.get() instanceof LeavesBlock || entry.get() instanceof SaplingBlock)
                consumer.accept(entry.get(), 0.3F);
        });

        BWGItems.ITEMS.stream().filter(item -> item.get().isEdible()).forEach(item -> consumer.accept(item.get(), 0.85F));
        consumer.accept(BWGBlocks.POISON_IVY.get(), 0.5F);
    }

    /**
     * Register a block as flammable.
     **/
    public static void registerFlammable(TriConsumer<Block, Integer, Integer> consumer) {
        BWGWoodSet.woodsets().forEach(set -> {
            consumer.accept(set.planks(), 5, 20);
            consumer.accept(set.slab(), 5, 20);
            consumer.accept(set.stairs(), 5, 20);
            consumer.accept(set.fence(), 5, 20);
            consumer.accept(set.fenceGate(), 5, 20);
            consumer.accept(set.logstem(), 5, 5);
            consumer.accept(set.strippedLogStem(), 5, 5);
            consumer.accept(set.wood(), 5, 5);
            consumer.accept(set.strippedWood(), 5, 5);
            consumer.accept(set.bookshelf(), 30, 20);
            if (set.leaves() != null) consumer.accept(set.leaves(), 30, 60);
        });
        consumer.accept(BWGWood.PALO_VERDE_LOG.get(), 5, 5);
        consumer.accept(BWGWood.STRIPPED_PALO_VERDE_LOG.get(), 5, 5);
        consumer.accept(BWGWood.PALO_VERDE_WOOD.get(), 5, 5);
        consumer.accept(BWGWood.STRIPPED_PALO_VERDE_WOOD.get(), 5, 5);
        consumer.accept(BWGWood.PALO_VERDE_LEAVES.get(), 30, 60);
        consumer.accept(BWGWood.IMBUED_BLUE_ENCHANTED_WOOD.get(), 5, 5);
        consumer.accept(BWGWood.IMBUED_GREEN_ENCHANTED_WOOD.get(), 5, 5);
        BWGBlocks.BLOCKS.forEach(entry -> {
            Block block = entry.get();
            if (block instanceof LeavesBlock)
                consumer.accept(block, 30, 60);
            else if (block instanceof CarpetBlock)
                consumer.accept(block, 60, 20);
            else if (block instanceof SweetBerryBushBlock || block instanceof FlowerBlock || block instanceof TallFlowerBlock || block instanceof TallGrassBlock)
                consumer.accept(block, 60, 100);
        });
        consumer.accept(BWGBlocks.POISON_IVY.get(), 15, 100);
    }
}
