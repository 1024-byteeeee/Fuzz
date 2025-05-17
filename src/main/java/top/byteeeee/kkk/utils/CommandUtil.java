/*
 * This file is part of the KKK project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 1024_byteeeee and contributors
 *
 * KKK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KKK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KKK. If not, see <https://www.gnu.org/licenses/>.
 */

package top.byteeeee.kkk.utils;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import java.util.Locale;

public class CommandUtil {
    public static boolean canUseCommand(FabricClientCommandSource source, Object commandLevel) {
        if (commandLevel instanceof Boolean) {
            return (Boolean) commandLevel;
        }
        if (commandLevel instanceof String) {
            final String levelStr = ((String) commandLevel).toLowerCase(Locale.ENGLISH);
            switch (levelStr) {
                case "true": return true;
                case "false": return false;
                case "ops": return source.hasPermissionLevel(2);
            }
            if (levelStr.length() == 1) {
                char c = levelStr.charAt(0);
                if (c >= '0' && c <= '4') {
                    return source.hasPermissionLevel(c - '0');
                }
            }
        }
        return false;
    }
}
