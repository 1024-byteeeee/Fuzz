/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.fuzz.utils;

import net.minecraft.ChatFormatting;

public enum Layout {
    BLACK(ChatFormatting.BLACK),
    DARK_BLUE(ChatFormatting.DARK_BLUE),
    DARK_GREEN(ChatFormatting.DARK_GREEN),
    DARK_AQUA(ChatFormatting.DARK_AQUA),
    DARK_RED(ChatFormatting.DARK_RED),
    DARK_PURPLE(ChatFormatting.DARK_PURPLE),
    GOLD(ChatFormatting.GOLD),
    GRAY(ChatFormatting.GRAY),
    DARK_GRAY(ChatFormatting.DARK_GRAY),
    BLUE(ChatFormatting.BLUE),
    GREEN(ChatFormatting.GREEN),
    AQUA(ChatFormatting.AQUA),
    RED(ChatFormatting.RED),
    LIGHT_PURPLE(ChatFormatting.LIGHT_PURPLE),
    YELLOW(ChatFormatting.YELLOW),
    WHITE(ChatFormatting.WHITE),
    OBFUSCATED(ChatFormatting.OBFUSCATED),
    BOLD(ChatFormatting.BOLD),
    STRIKETHROUGH(ChatFormatting.STRIKETHROUGH),
    UNDERLINE(ChatFormatting.UNDERLINE),
    ITALIC(ChatFormatting.ITALIC),
    RESET(ChatFormatting.RESET);

    private final ChatFormatting formatting;

    Layout(ChatFormatting formatting) {
        this.formatting = formatting;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    @Override
    public String toString() {
        return formatting.toString();
    }
}
//    "\u001B[0;30m",
//    "\u001B[0;34m",
//    "\u001B[0;32m",
//    "\u001B[0;36m",
//    "\u001B[0;31m",
//    "\u001B[0;35m",
//    "\u001B[0;33m",
//    "\u001B[0;37m",
//    "\u001B[0;30;1m",
//    "\u001B[0;34;1m",
//    "\u001B[0;32;1m",
//    "\u001B[0;36;1m",
//    "\u001B[0;31;1m",
//    "\u001B[0;35;1m",
//    "\u001B[0;33;1m",
//    "\u001B[0;37;1m",
//    "\u001B[5m",
//    "\u001B[21m",
//    "\u001B[9m",
//    "\u001B[4m",
//    "\u001B[3m",
//    ANSI_RESET,
