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

package top.byteeeee.fuzz.utils;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.fuzz.utils.compat.MessengerCompatFactory;

public class Messenger {
    public static MutableComponent s(Object text) {
        return MessengerCompatFactory.LiteralText(text.toString());
    }

    @NotNull
    public static MutableComponent f(MutableComponent text, Layout... formattings) {
        ChatFormatting[] chatFormattings = new ChatFormatting[formattings.length];

        for (int i = 0; i < formattings.length; i++) {
            chatFormattings[i] = formattings[i].getFormatting();
        }

        return text.withStyle(chatFormattings);
    }

    public static MutableComponent tr(String key, Object... args) {
        return MessengerCompatFactory.TranslatableText(key, args);
    }

    @NotNull
    public static MutableComponent copy(MutableComponent text) {
        return text.copy();
    }

    public static MutableComponent c(Object ... fields) {
        MutableComponent message = Component.literal("");

        for (Object o: fields) {
            if (o instanceof MutableComponent) {
                message.append((MutableComponent) o);
            }
        }

        return message;
    }

    public static MutableComponent sline() {
        return Messenger.s("-----------------------------------");
    }

    public static MutableComponent dline() {
        return Messenger.s("===================================");
    }

    @NotNull
    public static Style simpleCmdButtonStyle(String command, MutableComponent hoverText, Layout... hoverTextFormattings) {
        return emptyStyle()
            .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, command))
            .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, f(hoverText, hoverTextFormattings)));
    }

    @NotNull
    public static Style simpleCopyButtonStyle(String copyText, MutableComponent hoverText, Layout... hoverTextFormattings) {
        return emptyStyle()
            .withClickEvent(ClickEventUtil.event(ClickEventUtil.COPY_TO_CLIPBOARD, copyText))
            .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, f(hoverText, hoverTextFormattings)));
    }

    @SuppressWarnings("unused")
    public static Component endl() {
        return Messenger.s("\n");
    }

    public static void tell(FabricClientCommandSource source, MutableComponent text) {
        MessengerCompatFactory.sendFeedBack(source, text);
    }

    public static void tell(MutableComponent text, boolean actionBar) {
        if (actionBar) {
            ClientUtil.getCurrentPlayer().sendOverlayMessage(text);
        } else {
            ClientUtil.getCurrentPlayer().sendSystemMessage(text);
        }
    }

    public static void sendChatCommand(String text) {
        if (ClientUtil.getCurrentClient().player != null) {
            if (text.startsWith("/")) {
                text = text.substring(1);
            }
            ClientUtil.getCurrentPlayer().connection.sendCommand(text);
        }
    }

    public static Style emptyStyle() {
        return Style.EMPTY;
    }
}
