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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import top.byteeeee.fuzz.FuzzModClient;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    private static final ThreadLocal<Boolean> IS_LOCAL_PLAYER_TICKING = ThreadLocal.withInitial(() -> false);

    public static ClientPlayerEntity getCurrentPlayer() {
        return FuzzModClient.minecraftClient.player;
    }

    public static boolean isLocalPlayerSelf(Entity entity) {
        if (getCurrentPlayer() != null) {
            return entity.isPartOf(getCurrentPlayer());
        } else {
            return false;
        }
    }

    public static boolean isLocalPlayerTicking() {
        return IS_LOCAL_PLAYER_TICKING.get();
    }

    public static void setLocalPlayerTicking(boolean isLocalPlayerTicking) {
        IS_LOCAL_PLAYER_TICKING.set(isLocalPlayerTicking);
    }

    public static void removeLocalPlayerTicking() {
        IS_LOCAL_PLAYER_TICKING.remove();
    }

    public static MinecraftClient getCurrentClient() {
        return FuzzModClient.minecraftClient;
    }
}
