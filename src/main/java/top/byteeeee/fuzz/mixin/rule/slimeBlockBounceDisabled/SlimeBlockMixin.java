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

package top.byteeeee.fuzz.mixin.rule.slimeBlockBounceDisabled;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.Noop;
import top.byteeeee.fuzz.utils.ClientUtil;

@Mixin(SlimeBlock.class)
public abstract class SlimeBlockMixin {
    @WrapMethod(method = "bounce")
    private void slimeBlockBounceDisabled(Entity entity, Operation<Void> original) {
        if (FuzzSettings.slimeBlockSlowDownDisabled && entity.equals(ClientUtil.getCurrentPlayer())) {
            Noop.noop();
        } else {
            original.call(entity);
        }
    }
}
