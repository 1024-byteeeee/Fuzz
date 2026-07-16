package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(method = {"bypassesLandingEffects", "bypassesSteppingEffects"}, at = @At("RETURN"))
    private boolean skipSlimeBounceLogic(boolean original) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            Entity entity = (Entity) (Object) this;
            BlockPos belowPos = entity.getBlockPos().down();
            return EntityUtil.getEntityWorld(entity).getBlockState(belowPos).isOf(Blocks.SLIME_BLOCK);
        } else {
            return original;
        }
    }
}
