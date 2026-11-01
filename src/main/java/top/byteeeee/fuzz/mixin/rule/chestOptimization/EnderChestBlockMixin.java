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
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.byteeeee.fuzz.FuzzSettings;

//#if MC>=11700
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
//#else
//$$ import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
//#endif

@Environment(EnvType.CLIENT)
//#if MC>=11700
@Mixin(EnderChestBlock.class)
//#else
//$$ @Mixin(EnderChestBlockEntity.class)
//#endif
public abstract class EnderChestBlockMixin {
    //#if MC>=11700
    @ModifyReturnValue(method = "getTicker", at = @At("RETURN"))
    private <T extends BlockEntity> BlockEntityTicker<T> getTicker(BlockEntityTicker<T> original, @Local(argsOnly = true) Level level) {
        return FuzzSettings.chestOptimization && level.isClientSide() ? null : original;
    }
    //#else
    //$$ @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    //$$ private void skipAnimationTick(CallbackInfo ci) {
    //$$     Level level = ((BlockEntity) (Object) this).getLevel();
    //$$     if (FuzzSettings.chestOptimization && level != null && level.isClientSide) ci.cancel();
    //$$ }
    //#endif
}
