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

package top.byteeeee.kkk.helpers.function.quickKickFakePlayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import top.byteeeee.kkk.utils.ClientUtil;

@Environment(EnvType.CLIENT)
public class GetTargetPlayer {
    public static String getName() {
        MinecraftClient client = ClientUtil.getCurrentClient();
        ClientPlayerEntity player = ClientUtil.getCurrentPlayer();
        if (player == null) {
            return null;
        }

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
            return null;
        }

        EntityHitResult entityHit = (EntityHitResult) hit;
        Entity target = entityHit.getEntity();
        if (target instanceof PlayerEntity) {
            PlayerEntity targetPlayer = (PlayerEntity) target;
            return targetPlayer.getGameProfile().getName();
        }

        return null;
    }
}
