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

package top.byteeeee.fuzz.key;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;

import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import top.byteeeee.fuzz.utils.IdentifierUtil;
import top.byteeeee.fuzz.translations.Translator;

@Environment(EnvType.CLIENT)
public class KeyMappings {
    private static final Translator tr = new Translator("key");
    private static final KeyMapping.Category FUZZ_CATEGORY = KeyMapping.Category.register(IdentifierUtil.of("fuzz", "fuzz"));

    public static KeyMapping quickKickFakePlayer;
    public static KeyMapping quickDropFakePlayerAllItemStack;
    public static KeyMapping clearCoordCompass;

    public static void register() {
        quickKickFakePlayer = registerKeyBinding("quickKickFakePlayer");
        quickDropFakePlayerAllItemStack = registerKeyBinding("quickDropFakePlayerAllItemStack");
        clearCoordCompass = registerKeyBinding("clearCoordCompass");
    }

    private static KeyMapping registerKeyBinding(String translationKey) {
        FuzzKeyBinding fuzzKeyBinding = new FuzzKeyBinding(translationKey);
        return KeyMappingHelper.registerKeyMapping(fuzzKeyBinding);
    }

    private static class FuzzKeyBinding extends KeyMapping {
        private final String translationKey;

        public FuzzKeyBinding(String translationKey) {
            super(tr.tr(translationKey).getString(), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, FUZZ_CATEGORY);
            this.translationKey = translationKey;
        }

        @Override
        public @NonNull String getName() {
            return tr.tr(translationKey).getString();
        }
    }
}