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

package top.byteeeee.fuzz.mixin.rule.slimeBlockBounceDisabled;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
        //#if MC>=12102
        //$$ method = "travelMidAir",
        //#else
        method = "travel",
        //#endif
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getSlipperiness()F"
        )
    )
    private float slimeSlipperinessDisabled(Block block, Operation<Float> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (FuzzSettings.slimeBlockSlowDownDisabled && entity.equals(ClientUtil.getCurrentPlayer()) && block.equals(Blocks.SLIME_BLOCK)) {
            return Blocks.GRAY_CONCRETE.getSlipperiness();
        } else {
            return original.call(block);
        }
    }
}