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

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.text.BaseText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import top.byteeeee.fuzz.utils.compat.MessengerCompatFactory;

public class Messenger {
    public static BaseText s(Object text) {
        return MessengerCompatFactory.LiteralText(text.toString());
    }

    public static BaseText tr(String key, Object... args) {
        return MessengerCompatFactory.TranslatableText(key, args);
    }

    @SuppressWarnings("unused")
    public static Text endl() {
        return Messenger.s("\n");
    }

    public static void tell(FabricClientCommandSource source, BaseText text) {
        MessengerCompatFactory.sendFeedBack(source, text);
    }

    //#if MC<11900
    public static void tell(FabricClientCommandSource source, MutableText text) {
        MessengerCompatFactory.sendFeedBack(source, (BaseText) text);
    }
    //#endif

    public static void sendChatCommand(String text) {
        if (ClientUtil.getCurrentClient().player != null) {
            //#if MC<11900
            ClientUtil.getCurrentPlayer().sendChatMessage(text);
            //#else
            //$$ if (text.startsWith("/")) {
            //$$     text = text.substring(1);
            //$$ }
            //$$ ClientUtil.getCurrentPlayer().networkHandler.sendCommand(text);
            //#endif
        }
    }
}
