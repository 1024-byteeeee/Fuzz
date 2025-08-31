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

import net.minecraft.text.TranslatableText;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.byteeeee.fuzz.translations.FuzzTranslations;

@Mixin(TranslatableText.class)
public abstract class TranslatableTextMixin {
    @Shadow
    @Final
    private String key;

    @ModifyArg(
        method = "updateTranslations",
        at = @At(
            value = "INVOKE",
            //#if MC<11800
            target = "Lnet/minecraft/text/TranslatableText;setTranslation(Ljava/lang/String;)V"
            //#else
            //$$ target = "Lnet/minecraft/text/TranslatableText;forEachPart(Ljava/lang/String;Ljava/util/function/Consumer;)V"
            //#endif
        )
    )
    private String applyModTranslation(String vanillaTranslatedFormattingString) {
        if (this.key.startsWith("fuzz.") && vanillaTranslatedFormattingString.equals(this.key)) {
            String modTranslated = FuzzTranslations.translateKeyToFormattedString(FuzzTranslations.getServerLanguage(), this.key);
            if (modTranslated != null) {
                return modTranslated;
            }
        }
        return vanillaTranslatedFormattingString;
    }
}
