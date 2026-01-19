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

package top.byteeeee.fuzz.mixin.rule.chestOptimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Pack.class)
public abstract class PackMixin {
    @WrapOperation(
        method = "getCompatibility",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/Pack$Metadata;compatibility()Lnet/minecraft/server/packs/repository/PackCompatibility;"
        )
    )
    private PackCompatibility setCompatible(Pack.Metadata metadata, Operation<PackCompatibility> original) {
        Pack pack = (Pack) (Object) this;

        if (pack != null && pack.getId().equals("fuzz_mod:chest_optimization")) {
            return PackCompatibility.COMPATIBLE;
        } else {
            return original.call(metadata);
        }
    }
}
