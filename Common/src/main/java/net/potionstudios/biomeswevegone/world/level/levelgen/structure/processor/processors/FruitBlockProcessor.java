package net.potionstudios.biomeswevegone.world.level.levelgen.structure.processor.processors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.potionstudios.biomeswevegone.world.level.block.plants.tree.fruit.BWGFruitBlock;
import net.potionstudios.biomeswevegone.world.level.levelgen.structure.processor.BWGCustomStructureProcessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FruitBlockProcessor extends StructureProcessor {

	public static final MapCodec<FruitBlockProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("fruit_block")
					.forGetter((processor) -> processor.fruitBlock)).apply(instance, FruitBlockProcessor::new));

	private final BWGFruitBlock fruitBlock;

	public FruitBlockProcessor(BWGFruitBlock fruitBlock) {
		this.fruitBlock = fruitBlock;
	}

	private FruitBlockProcessor(Block fruitBlock) {
		this((BWGFruitBlock) fruitBlock);
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader level, @NotNull BlockPos offset, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo relativeBlockInfo, @NotNull StructurePlaceSettings settings) {
		return super.processBlock(level, offset, pos, blockInfo, relativeBlockInfo, settings);
	}

	@Override
	public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(@NotNull ServerLevelAccessor serverLevel, @NotNull BlockPos offset, @NotNull BlockPos pos, @NotNull List<StructureTemplate.StructureBlockInfo> originalBlockInfos, @NotNull List<StructureTemplate.StructureBlockInfo> processedBlockInfos, @NotNull StructurePlaceSettings settings) {
		List<StructureTemplate.StructureBlockInfo> newInfo = new java.util.ArrayList<>(List.copyOf(processedBlockInfos));

		processedBlockInfos.stream().filter(blockInfo -> blockInfo.state().is(this.fruitBlock.getLeaves())).forEach(leavesBlockInfo -> {
			BlockPos leavesPos = leavesBlockInfo.pos();
			if (serverLevel.getBlockState(leavesPos.below()).isAir() && serverLevel.getRandom().nextBoolean()) {
				newInfo.stream().filter(blockInfo -> blockInfo.pos().equals(leavesPos.below())).findFirst().ifPresent(fruitSpot -> {
					newInfo.remove(fruitSpot);
					newInfo.add(new StructureTemplate.StructureBlockInfo(fruitSpot.pos(), this.fruitBlock.defaultBlockState().setValue(BWGFruitBlock.AGE, serverLevel.getRandom().nextInt(BWGFruitBlock.MAX_AGE)), fruitSpot.nbt()));
				});
			}
		});
		return newInfo;
	}

	@Override
	protected @NotNull StructureProcessorType<?> getType() {
		return BWGCustomStructureProcessors.FRUIT_BLOCK_PROCESSOR.get();
	}
}
