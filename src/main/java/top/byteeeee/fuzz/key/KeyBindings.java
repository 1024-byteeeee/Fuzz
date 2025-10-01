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
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;

import org.lwjgl.glfw.GLFW;

//#if MC>=12109
//$$ import top.byteeeee.fuzz.FuzzModClient;
//$$ import top.byteeeee.fuzz.utils.IdentifierUtil;
//#endif
import top.byteeeee.fuzz.translations.Translator;

@Environment(EnvType.CLIENT)
public class KeyBindings {
    private static final Translator tr = new Translator("key");
    //#if MC>=12109
    //$$ private static final KeyBinding.Category FUZZ_CATEGORY = KeyBinding.Category.create(IdentifierUtil.of("fuzz", "fuzz"));
    //#endif
    private static final MutableText CATEGORY = tr.tr("category.fuzz");

    public static KeyBinding quickKickFakePlayer;
    public static KeyBinding quickDropFakePlayerAllItemStack;
    public static KeyBinding clearCoordCompass;

    public static void register() {
        quickKickFakePlayer = registerKeyBinding("quickKickFakePlayer");
        quickDropFakePlayerAllItemStack = registerKeyBinding("quickDropFakePlayerAllItemStack");
        clearCoordCompass = registerKeyBinding("clearCoordCompass");
    }

    private static KeyBinding registerKeyBinding(String translationKey) {
        FuzzKeyBinding fuzzKeyBinding = new FuzzKeyBinding(translationKey);
        return KeyBindingHelper.registerKeyBinding(fuzzKeyBinding);
    }

    private static class FuzzKeyBinding extends KeyBinding {
        private final String translationKey;

        public FuzzKeyBinding(String translationKey) {
            super(
                tr.tr(translationKey).getString(), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                //#if MC>=12109
                //$$ FUZZ_CATEGORY
                //#else
                CATEGORY.getString()
                //#endif
            );
            this.translationKey = translationKey;
        }

        @Override
        public String getTranslationKey() {
            return tr.tr(translationKey).getString();
        }
    }
}