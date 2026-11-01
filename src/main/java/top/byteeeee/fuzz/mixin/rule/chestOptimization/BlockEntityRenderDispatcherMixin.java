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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//#if MC>=12109
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
//#endif
import net.minecraft.world.level.block.entity.BlockEntity;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.helpers.rule.commandAnimatedFreeze.EntityRenderBlock;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;", at = @At("RETURN"))
    //#if MC>=12109
    private <E extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRenderer<E, S> get(BlockEntityRenderer<E, S> original, E blockEntity) {
    //#else
    //$$ private <E extends BlockEntity> BlockEntityRenderer<E> get(BlockEntityRenderer<E> original, E blockEntity) {
    //#endif
        return EntityRenderBlock.isOf(blockEntity.getClass()) && FuzzSettings.chestOptimization ? null : original;
    }
}
