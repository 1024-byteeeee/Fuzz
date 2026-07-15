package top.byteeeee.fuzz.mixin.hooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.player.LocalPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickStart(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (ClientUtil.isSelf(player)) {
            ClientUtil.IS_LOCAL_PLAYER_TICKING.set(true);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickEnd(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (ClientUtil.isSelf(player)) {
            ClientUtil.IS_LOCAL_PLAYER_TICKING.remove();
        }
    }
}
