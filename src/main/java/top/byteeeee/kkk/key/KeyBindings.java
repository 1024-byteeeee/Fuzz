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

package top.byteeeee.kkk.key;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.BaseText;

import org.lwjgl.glfw.GLFW;

import top.byteeeee.kkk.translations.Translator;

@Environment(EnvType.CLIENT)
public class KeyBindings {
    private static final Translator tr = new Translator("keyBinding");
    private static final BaseText CATEGORY = tr.tr("category");

    public static KeyBinding quickKickFakePlayer;
    public static KeyBinding quickDropFakePlayerAllItemStack;

    public static void register() {
        quickKickFakePlayer = registerKeyBinding(generalKeyBinding(tr.tr("quickKickFakePlayer.name")));
        quickDropFakePlayerAllItemStack = registerKeyBinding(generalKeyBinding(tr.tr("quickDropFakePlayerAllItemStack.name")));
    }

    private static KeyBinding generalKeyBinding(BaseText translationKey) {
        return new KeyBinding(translationKey.getString(), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY.getString());
    }

    private static KeyBinding registerKeyBinding(KeyBinding binding) {
        return KeyBindingHelper.registerKeyBinding(binding);
    }
}
