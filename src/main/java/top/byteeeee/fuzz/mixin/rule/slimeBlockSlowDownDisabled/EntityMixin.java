package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(method = "isSuppressingBounce", at = @At("RETURN"))
    private boolean skipSlimeBounceLogic(boolean original) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            Entity entity = (Entity) (Object) this;
            BlockPos belowPos = entity.getBlockPosBelowThatAffectsMyMovement();
            return EntityUtil.getEntityWorld(entity).getBlockState(belowPos).is(Blocks.SLIME_BLOCK);
        } else {
            return original;
        }
    }
}
