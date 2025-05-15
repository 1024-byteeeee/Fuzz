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

package top.byteeeee.kkk.mixin.function.bedRockFlying;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "move", at = @At("TAIL"))
    private void onMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (KKKSettings.bedRockFlying) {
            ClientPlayerEntity player = ClientUtil.getCurrentPlayer();
            if (
                movementType.equals(MovementType.SELF) &&
                //#if MC>=11700
                //$$ player.getAbilities().flying &&
                //#else
                player.abilities.flying &&
                //#endif
                !((ClientPlayerEntityInvoker) player).invokerHasMovementInput()
            ) {
                player.setVelocity(Vec3d.ZERO);
            }
        }
    }
}