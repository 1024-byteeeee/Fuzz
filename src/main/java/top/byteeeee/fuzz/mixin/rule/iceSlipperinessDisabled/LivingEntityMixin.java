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

package top.byteeeee.fuzz.mixin.rule.iceSlipperinessDisabled;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.iceSlipperinessDisabled.IceFamily;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
        method = "travelInAir",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
        )
    )
    private float iceSlipperinessDisabled(Block block, Operation<Float> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (FuzzSettings.iceSlipperinessDisabled && entity.equals(ClientUtil.getCurrentPlayer()) && IceFamily.isIce(block)) {
            return Blocks.GRAY_CONCRETE.getFriction();
        } else {
            return original.call(block);
        }
    }
}