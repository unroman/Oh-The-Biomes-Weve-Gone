package net.potionstudios.biomeswevegone.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends BushBlock implements BonemealableBlock {
    public CropBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @reason Allows crops to be placed on Lush Farmland.
     * @author Joseph T. McQuigg
     */
    @Inject(method = "mayPlaceOn", at = @At("RETURN"), cancellable = true)
    protected void mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.getBlock() instanceof FarmBlock);
    }
}
