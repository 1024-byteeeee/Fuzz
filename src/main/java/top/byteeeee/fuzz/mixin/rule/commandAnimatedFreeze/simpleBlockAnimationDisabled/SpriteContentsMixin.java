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

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.texture.SpriteContents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import top.byteeeee.fuzz.FuzzSettings;

@GameVersion(version = "Minecraft >= 1.19.3")
@Environment(EnvType.CLIENT)
@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin {
    @Inject(
        //#if MC>=12111
        method = "createAnimationState",
        //#else
        //$$ method = "createTicker",
        //#endif
        at = @At("HEAD"),
        cancellable = true
    )
    private void noAnimation(CallbackInfoReturnable<?> cir) {
        for (String block : FuzzSettings.animationDisableList) {
            if (((SpriteContents) (Object) this).name().getPath().endsWith(block)) {
                cir.setReturnValue(null);
                return;
            }
        }
    }
}
