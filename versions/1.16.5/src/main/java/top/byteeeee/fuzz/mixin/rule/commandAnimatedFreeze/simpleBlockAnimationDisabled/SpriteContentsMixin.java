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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;

@GameVersion(version = "Minecraft <= 1.16.5")
@Environment(EnvType.CLIENT)
@Mixin(TextureAtlasSprite.class)
public abstract class SpriteContentsMixin {
    @Inject(method = "cycleFrames", at = @At("HEAD"), cancellable = true)
    private void noAnimation(CallbackInfo ci) {
        String path = ((TextureAtlasSprite) (Object) this).getName().getPath();
        for (String block : FuzzSettings.animationDisableList) {
            if (path.endsWith(block)) {
                ci.cancel();
                return;
            }
        }
    }
}
