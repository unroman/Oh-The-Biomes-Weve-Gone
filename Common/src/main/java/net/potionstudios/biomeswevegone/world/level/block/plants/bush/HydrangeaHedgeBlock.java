package net.potionstudios.biomeswevegone.world.level.block.plants.bush;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.potionstudios.biomeswevegone.tags.BWGBlockTags;
import net.potionstudios.biomeswevegone.tags.BWGItemTags;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import org.jetbrains.annotations.NotNull;

public class HydrangeaHedgeBlock extends BWGPlacementBushBlock{
    public HydrangeaHedgeBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.AZALEA),
                Shapes.or(Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D)),
                BWGBlockTags.HYDRANGEA_BUSH_PLACEABLE);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.getItemInHand(hand).is(BWGItemTags.SHEARS)) {
            level.setBlockAndUpdate(pos, BWGBlocks.HYDRANGEA_BUSH.getBlockState());
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + level.random.nextDouble(), pos.getY() + 1.0D, pos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
