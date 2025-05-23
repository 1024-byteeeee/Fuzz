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

package top.byteeeee.fuzz.translations;

import net.minecraft.text.BaseText;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.utils.Messenger;

public class Translator {
    private final String translationPath;

    public Translator(String translationPath) {
        this.translationPath = translationPath;
    }

    public BaseText tr(String key, Object... args) {
        String translationKey = FuzzModClient.MOD_ID + "." + this.translationPath + "." + key;
        return Messenger.tr(translationKey, args);
    }

    public String getFuncNameTrKey(String name) {
        return String.format("fuzz.settings.%s.name", name);
    }

    public String getFuncDescTrKey(String name) {
        return String.format("fuzz.settings.%s.desc", name);
    }
}
