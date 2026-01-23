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

package top.byteeeee.fuzz.mixin.rule.cobwebSlowDownDisabled;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.Noop;
import top.byteeeee.fuzz.utils.ClientUtil;

@Mixin(CobwebBlock.class)
public abstract class CobwebMixin {
    @WrapOperation(
        method = "onEntityCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;slowMovement(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Vec3d;)V"
        )
    )
    private void slowDownDisabled(Entity entity, BlockState state, Vec3d multiplier, Operation<Void> original) {
        if (FuzzSettings.cobwebSlowDownDisabled && entity.equals(ClientUtil.getCurrentPlayer())) {
            Noop.noop();
        } else {
            original.call(entity, state, multiplier);
        }
    }
}
