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
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.fuzz.utils.Messenger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class, priority = 1688)
public abstract class ChatHudMixin {

    @Unique
    private static final Pattern COORD_PATTERN = Pattern.compile("(-?\\d+(\\.\\d+)?)[\\s,~]+(-?\\d+(\\.\\d+)?)[\\s,~]+(-?\\d+(\\.\\d+)?)");

    @Unique
    private static final Translator tr = new Translator("rule_feedback.parseCoordInMessage");

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
        String content = original.getString();
        Matcher matcher = COORD_PATTERN.matcher(content);

        if (!matcher.find()) {
            return original;
        }

        matcher.reset();

        BaseText result = Messenger.s("");
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.append(Messenger.s(content.substring(lastEnd, matcher.start())));
            }

            String x = matcher.group(1);
            String y = matcher.group(3);
            String z = matcher.group(5);
            String coords = x + " " + y + " " + z;

            BaseText coordText = Messenger.s(matcher.group());
            coordText.setStyle(Style.EMPTY.withColor(Formatting.GREEN).withUnderline(true).withClickEvent(
                ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/coordCompass set " + x + " " + y + " " + z)
            ));

            coordText.setStyle(coordText.getStyle().withHoverEvent(
                HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, tr.tr("hover_text", coords).formatted(Formatting.YELLOW))
            ));

            result.append(coordText);
            lastEnd = matcher.end();
        }

        if (lastEnd < content.length()) {
            result.append(Messenger.s(content.substring(lastEnd)));
        }

        return FuzzSettings.parseCoordInMessage ? result : original;
    }
}