package top.byteeeee.fuzz.mixin.rule.honeyBlockSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HoneyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(Block.class)
public abstract class BlockMixin {
    @ModifyReturnValue(method = "getFriction", at = @At("RETURN"))
    private float modifyFriction(float original){
        if (!ClientUtil.IS_LOCAL_PLAYER_TICKING.get()) {
            return original;
        }

        Block block = (Block) (Object) this;
        if (FuzzSettings.honeyBlockSlowDownDisabled && block instanceof HoneyBlock) {
            return Blocks.TNT.getFriction();
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getSpeedFactor", at = @At("RETURN"))
    private float modifySpeedFactor(float original) {
        if (!ClientUtil.IS_LOCAL_PLAYER_TICKING.get()) {
            return original;
        }

        Block block = (Block) (Object) this;
        if (FuzzSettings.honeyBlockSlowDownDisabled && block instanceof HoneyBlock) {
            return Blocks.TNT.getSpeedFactor();
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getJumpFactor", at = @At("RETURN"))
    private float modifyJumpFactor(float original) {
        if (!ClientUtil.IS_LOCAL_PLAYER_TICKING.get()) {
            return original;
        }

        Block block = (Block) (Object) this;
        if (FuzzSettings.honeyBlockSlowDownDisabled && block instanceof HoneyBlock) {
            return Blocks.TNT.getJumpFactor();
        } else {
            return original;
        }
    }
}
