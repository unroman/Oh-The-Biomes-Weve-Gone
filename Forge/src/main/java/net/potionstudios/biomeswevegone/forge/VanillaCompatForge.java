package net.potionstudios.biomeswevegone.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.potionstudios.biomeswevegone.world.item.tools.ToolInteractions;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import net.potionstudios.biomeswevegone.world.level.block.BlockFeatures;
import net.potionstudios.biomeswevegone.world.level.levelgen.biome.BWGBiomes;
import net.potionstudios.biomeswevegone.world.level.levelgen.feature.placed.BWGOverworldVegationPlacedFeatures;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Used for Vanilla compatibility on the Forge platform.
 * @author Joseph T. McQuigg
 */
public class VanillaCompatForge {
    public static void init() {
        ToolInteractions.registerStrippableBlocks((block, stripped) -> {
            AxeItem.STRIPPABLES = new HashMap<>(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.put(block, stripped);
        });
        BlockFeatures.registerFlammable(((FireBlock) Blocks.FIRE)::setFlammable);
        BlockFeatures.registerCompostables((item, chance) -> ComposterBlock.add(chance, item));
        ToolInteractions.registerFlattenables(ShovelItem.FLATTENABLES::put);
        ToolInteractions.registerTillables(HoeItem.TILLABLES::put);
    }

    public static void registerVanillaCompatEvents(IEventBus bus) {
        bus.addListener(VanillaCompatForge::registerFuels);
        bus.addListener(VanillaCompatForge::onBoneMealUse);
    }

    /**
     * Register fuels for the furnace.
     * @see FurnaceFuelBurnTimeEvent
     */
    private static void registerFuels(final FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(BWGBlocks.PEAT.get().asItem()))
            event.setBurnTime(1200);
    }

    private static void onBoneMealUse(final BonemealEvent event) {
        if (event.getLevel().isClientSide()) return;
        ServerLevel level = (ServerLevel) event.getLevel();
        if (event.getBlock().is(Blocks.GRASS_BLOCK) && level.getBiome(event.getPos()).is(BWGBiomes.PRAIRIE)) {
            BlockPos blockPos = event.getPos().above();
            BlockState blockState = BWGBlocks.PRAIRIE_GRASS.get().defaultBlockState();
            Optional<Holder.Reference<PlacedFeature>> optional = level.registryAccess()
                    .registryOrThrow(Registries.PLACED_FEATURE)
                    .getHolder(BWGOverworldVegationPlacedFeatures.PRAIRIE_GRASS_BONEMEAL);

            label49:
            for(int i = 0; i < 128; ++i) {
                BlockPos blockPos2 = blockPos;
                RandomSource random = level.getRandom();
                for(int j = 0; j < i / 16; ++j) {
                    blockPos2 = blockPos2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (!level.getBlockState(blockPos2.below()).is(Blocks.GRASS_BLOCK) || level.getBlockState(blockPos2).isCollisionShapeFullBlock(level, blockPos2))
                        continue label49;
                }

                BlockState blockState2 = level.getBlockState(blockPos2);
                if (blockState2.is(blockState.getBlock()) && random.nextInt(10) == 0)
                    ((BonemealableBlock)blockState.getBlock()).performBonemeal(level, random, blockPos2, blockState2);


                if (blockState2.isAir()) {
                    Holder<PlacedFeature> holder;
                    if (random.nextInt(8) == 0) {
                        List<ConfiguredFeature<?, ?>> list = level.getBiome(blockPos2).value().getGenerationSettings().getFlowerFeatures();
                        if (list.isEmpty()) continue;

                        holder = ((RandomPatchConfiguration) list.get(0).config()).feature();
                    } else {
                        if (!optional.isPresent()) continue;
                        holder = optional.get();
                    }

                    holder.value().place(level, level.getChunkSource().getGenerator(), random, blockPos2);
                }
            }
            event.setResult(Event.Result.ALLOW);
        }
    }
}
