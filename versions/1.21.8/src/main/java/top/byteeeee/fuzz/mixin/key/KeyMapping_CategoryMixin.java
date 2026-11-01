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

package top.byteeeee.fuzz.mixin.key;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.resources.language.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import top.byteeeee.fuzz.translations.FuzzTranslations;

import java.util.Objects;

@GameVersion(version = "Minecraft <= 1.21.8")
@Environment(EnvType.CLIENT)
@Mixin(I18n.class)
public abstract class KeyMapping_CategoryMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void translateCategory(String key, Object[] args, CallbackInfoReturnable<String> cir) {
        if (Objects.equals(key, "fuzz.key.category.fuzz")) {
            String translated = FuzzTranslations.translateKeyToFormattedString(
                FuzzTranslations.getLanguage(), key);
            if (translated != null) {
                cir.setReturnValue(translated);
            }
        }
    }
}
