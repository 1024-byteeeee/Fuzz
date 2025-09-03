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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.util.Formatting;

import top.byteeeee.fuzz.translations.Translator;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class CommandUtil {
    private static final Translator tr = new Translator("command.util");

    public static int checkEnabled(FabricClientCommandSource source, boolean condition, String functionName, Supplier<Integer> action) {
        if (!condition) {
            Messenger.tell(source, tr.tr("check.need_enable_rule", functionName).formatted(Formatting.RED));
            return 0;
        }

        return action.get();
    }
}
