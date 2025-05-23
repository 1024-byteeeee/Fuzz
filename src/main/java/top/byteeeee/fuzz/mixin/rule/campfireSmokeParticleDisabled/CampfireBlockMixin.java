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

package top.byteeeee.fuzz.mixin.rule.campfireSmokeParticleDisabled;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.CampfireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.Noop;

@Environment(EnvType.CLIENT)
@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @WrapMethod(method = "spawnSmokeParticle")
    private static void noSpawnParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke, Operation<Void> original) {
        if (FuzzSettings.campfireSmokeParticleDisabled) {
            Noop.noop();
        } else {
            original.call(world, pos, isSignal, lotsOfSmoke);
        }
    }
}
