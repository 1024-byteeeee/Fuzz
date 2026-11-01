/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 1024_byteeeee and contributors
 *
 * Fuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fuzz. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.mixin.rule.commandAnimatedFreeze.simpleBlockAnimationDisabled;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.texture.SpriteContents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import top.byteeeee.fuzz.FuzzSettings;

@Environment(EnvType.CLIENT)
@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin {
    //#if MC>=11700
    //#if MC>=12111
    @Inject(method = "createAnimationState", at = @At("HEAD"), cancellable = true)
    //#elseif MC>=11903
    //$$ @Inject(method = "createTicker", at = @At("HEAD"), cancellable = true)
    //#else
    //$$ @Inject(method = "getAnimationTicker", at = @At("HEAD"), cancellable = true)
    //#endif
    private void noAnimation(CallbackInfoReturnable<?> cir) {
        for (String block : FuzzSettings.animationDisableList) {
            if (((SpriteContents) (Object) this).name().getPath().endsWith(block)) {
                cir.setReturnValue(null);
                return;
            }
        }
    }
    //#else
    //$$ @org.spongepowered.asm.mixin.injection.Inject(method = "cycleFrames", at = @At("HEAD"), cancellable = true)
    //$$ private void noAnimation(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
    //$$     String path = ((net.minecraft.client.renderer.texture.TextureAtlasSprite) (Object) this).getName().getPath();
    //$$     for (String block : FuzzSettings.animationDisableList) {
    //$$         if (path.endsWith(block)) {
    //$$             ci.cancel();
    //$$             return;
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
