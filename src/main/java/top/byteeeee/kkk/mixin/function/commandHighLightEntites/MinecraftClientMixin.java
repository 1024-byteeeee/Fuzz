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

package top.byteeeee.kkk.mixin.function.commandHighLightEntites;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.kkk.KKKSettings;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
    private boolean clairvoyance(boolean original, Entity entity) {
        if (KKKSettings.commandhighLightEntities && KKKSettings.highlightedEntities.contains(EntityType.getId(entity.getType()).toString())) {
            return true;
        } else {
            return original;
        }
    }
}
