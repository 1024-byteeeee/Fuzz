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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.byteeeee.fuzz.FuzzSettings;

//#if MC<12105
//$$ import net.minecraft.world.level.block.ChestBlock;
//$$ import net.minecraft.world.level.block.EnderChestBlock;
//$$ import net.minecraft.world.level.block.RenderShape;
//#endif

//#if MC<12105
//$$ @Mixin({ChestBlock.class, EnderChestBlock.class})
//#else
@Mixin(top.byteeeee.fuzz.utils.compat.DummyClass.class)
//#endif
public abstract class ChestRenderShapeMixin {
    //#if MC<12105
    //$$ @ModifyReturnValue(method = "getRenderShape", at = @At("RETURN"))
    //$$ private RenderShape useBakedChestModel(RenderShape original) {
    //$$     return FuzzSettings.chestOptimization ? RenderShape.MODEL : original;
    //$$ }
    //#endif
}
