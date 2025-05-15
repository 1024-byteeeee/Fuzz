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

package top.byteeeee.kkk.mixin.function.bubbleColumnInteractDisabled;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.entity.Entity;
//#if MC>=12105
//$$ import net.minecraft.entity.EntityCollisionHandler;
//#endif
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.helpers.Noop;
import top.byteeeee.kkk.utils.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin {
    @WrapMethod(method = "onEntityCollision")
    private void onEntityCollision(
        BlockState state, World world, BlockPos pos, Entity entity,
        //#if MC>=12105
        //$$ EntityCollisionHandler handler,
        //#endif
        Operation<Void> original
    ) {
        if (KKKSettings.bubbleColumnInteractDisabled && entity.equals(ClientUtil.getCurrentPlayer())) {
            Noop.noop();
        } else {
            original.call(
                state, world, pos, entity
                //#if MC>=12105
                //$$ ,handler
                //#endif
            );
        }
    }
}
