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

package top.byteeeee.fuzz.mixin.rule.jumpDelayDisabled;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    private int noJumpDelay;

    @Inject(method = "tick", at = @At("HEAD"))
    private void removeJumpDelay1(CallbackInfo ci) {
        if (FuzzSettings.jumpDelayDisabled) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity.equals(ClientUtil.getCurrentPlayer())) {
                this.noJumpDelay = 0;
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void removeJumpDelay2(CallbackInfo ci) {
        if (FuzzSettings.jumpDelayDisabled) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity.equals(ClientUtil.getCurrentPlayer())) {
                this.noJumpDelay = 0;
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void removeJumpDelay3(CallbackInfo ci) {
        if (FuzzSettings.jumpDelayDisabled) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity.equals(ClientUtil.getCurrentPlayer())) {
                this.noJumpDelay = 0;
            }
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void removeJumpDelay4(CallbackInfo ci) {
        if (FuzzSettings.jumpDelayDisabled) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity.equals(ClientUtil.getCurrentPlayer())) {
                this.noJumpDelay = 0;
            }
        }
    }
}
