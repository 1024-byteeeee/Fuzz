/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
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

package top.byteeeee.fuzz.mixin.rule.fogRenderDisabled;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.BackgroundRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//#if MC<12102
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#else
//$$ import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//$$ import net.minecraft.client.render.Fog;
//#endif

import top.byteeeee.annotationtoolbox.annotation.GameVersion;
import top.byteeeee.fuzz.FuzzSettings;

@GameVersion(version = "Minecraft < 1.21.6")
@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    //#if MC<11700
    @ModifyExpressionValue(
        method = "applyFog",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"
        )
    )
    private static boolean setFogState(boolean original) {
        return FuzzSettings.fogRenderDisabled || original;
    }
    //#endif

    //#if MC<12102
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void setFogDensity(CallbackInfo ci) {
        if (FuzzSettings.fogRenderDisabled) {
            //#if MC<11700
            RenderSystem.fogDensity(0.0F);
            //#else
            //$$ RenderSystem.setShaderFogStart(Float.MAX_VALUE);
            //$$ RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
            //#endif
        }
    }
    //#endif

    //#if MC>12102 && MC<12106
    //$$ @ModifyReturnValue(method = "applyFog", at = @At("RETURN"))
    //$$ private static Fog noFog(Fog original) {
    //$$     return FuzzSettings.fogRenderDisabled ? Fog.DUMMY : original;
    //$$ }
    //#endif
}
