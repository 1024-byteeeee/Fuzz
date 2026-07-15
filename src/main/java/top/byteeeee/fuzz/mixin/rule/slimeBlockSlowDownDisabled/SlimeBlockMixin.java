package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void aVoid(BlockBehaviour.Properties properties, CallbackInfo ci) {
        properties.jumpFactor(Blocks.TNT.getJumpFactor());
        properties.speedFactor(Blocks.TNT.getSpeedFactor());
        properties.friction(Blocks.TNT.getFriction());
    }
}
