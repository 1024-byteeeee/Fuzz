package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.SlimeBlock;
import net.minecraft.block.TransparentBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin extends TransparentBlock {
    protected SlimeBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void skipOnSteppedOnLogic(CallbackInfo ci) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            ci.cancel();
        }
    }
}
