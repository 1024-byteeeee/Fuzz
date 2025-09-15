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

package top.byteeeee.fuzz.mixin.rule.parseCoordInMessage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.parseCoordInMessage.TextProcessor;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class, priority = 1688)
public abstract class ChatHudMixin {
    @ModifyVariable(
        //#if MC>=11900
        //$$ method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        //#else
        method = "addMessage(Lnet/minecraft/text/Text;)V",
        //#endif
        at = @At("HEAD"),
        argsOnly = true
    )
    private Text parseCoordInMessage(Text original) {
        return FuzzSettings.parseCoordInMessage ? TextProcessor.processTextForCoordinates(original) : original;
    }
}
