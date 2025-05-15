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

package top.byteeeee.kkk.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.function.quickKickFakePlayer.GetTargetPlayer;
import top.byteeeee.kkk.key.KeyBindings;
import top.byteeeee.kkk.utils.Messenger;

@Environment(EnvType.CLIENT)
public class ClientEvent {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::quickKickFakePlayer);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::quickDropFakePlayerAllItemStack);
    }

    private static class ClientEventHandler {
        private static void quickKickFakePlayer(MinecraftClient client) {
            while (KKKSettings.quickKickFakePlayer && KeyBindings.quickKickFakePlayer.wasPressed()) {
                if (client.player != null) {
                    String name = GetTargetPlayer.getName();
                    if (name != null && !name.isEmpty()) {
                        Messenger.sendChatCommand(String.format("/player %s kill", name));
                    }
                }
            }
        }

        private static void quickDropFakePlayerAllItemStack(MinecraftClient client) {
            while(KKKSettings.quickDropFakePlayerAllItemStack && KeyBindings.quickDropFakePlayerAllItemStack.wasPressed()) {
                if (client.player != null) {
                    String name = GetTargetPlayer.getName();
                    if (name != null && !name.isEmpty()) {
                        Messenger.sendChatCommand(String.format("/player %s dropStack all", name));
                    }
                }
            }
        }
    }
}
