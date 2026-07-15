/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 1024_byteeeee and contributors
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

package top.byteeeee.fuzz.mixin.rule.slimeBlockSlowDownDisabled;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//#if MC<260200
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
//#endif
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SlimeBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin extends HalfTransparentBlock {
    public SlimeBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    private void tt(CallbackInfo ci) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            ci.cancel();
        }
    }

    //#if MC<260200
    @Inject(method = "updateEntityMovementAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void skipSlimeBounceLogic(BlockGetter level, Entity entity, CallbackInfo ci) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && ClientUtil.isLocalPlayerTicking()) {
            super.updateEntityMovementAfterFallOn(level, entity);
            ci.cancel();
        }
    }
    //#endif
}
