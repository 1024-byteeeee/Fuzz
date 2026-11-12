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

package top.byteeeee.fuzz.mixin.translations;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.translations.FuzzTranslations;
import top.byteeeee.fuzz.translations.TranslationConstants;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(TranslatableContents.class)
public abstract class TranslatableTextMixin {
    @Shadow
    @Final
    private String key;

    @Shadow
    private Language decomposedWith;

    @Unique
    private String fuzz$decomposedLanguage;

    @Inject(method = "decompose", at = @At("HEAD"))
    private void invalidateFuzzTranslation(CallbackInfo ci) {
        if (this.key.startsWith(TranslationConstants.TRANSLATION_KEY_PREFIX)) {
            String language = FuzzTranslations.getLanguage();
            if (!language.equals(this.fuzz$decomposedLanguage)) {
                this.fuzz$decomposedLanguage = language;
                this.decomposedWith = null;
            }
        }
    }

    @ModifyArg(
        method = "decompose",
        at = @At(
            value = "INVOKE",
            //#if MC>=11900
            target = "Lnet/minecraft/network/chat/contents/TranslatableContents;decomposeTemplate(Ljava/lang/String;Ljava/util/function/Consumer;)V"
            //#elseif MC>=11800
            //$$ target = "Lnet/minecraft/network/chat/TranslatableComponent;decomposeTemplate(Ljava/lang/String;Ljava/util/function/Consumer;)V"
            //#else
            //$$ target = "Lnet/minecraft/network/chat/TranslatableComponent;decomposeTemplate(Ljava/lang/String;)V"
            //#endif
        )
    )
    private String applyModTranslation(String vanillaTranslatedFormattingString) {
        if (this.fuzz$decomposedLanguage != null && Objects.equals(vanillaTranslatedFormattingString, this.key)) {
            String modTranslated = FuzzTranslations.translateKeyToFormattedString(this.fuzz$decomposedLanguage, this.key);
            if (modTranslated != null) {
                return modTranslated;
            }
        }

        return vanillaTranslatedFormattingString;
    }
}
