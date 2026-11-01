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

package top.byteeeee.fuzz.mixin.rule.fogColor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.validators.HexValidator;

//#if MC>=12106
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
//#else
//$$ import net.minecraft.world.level.biome.BiomeSpecialEffects;
//#endif

@Environment(EnvType.CLIENT)
//#if MC>=12106
@Mixin(FogRenderer.class)
//#else
//$$ @Mixin(BiomeSpecialEffects.class)
//#endif
public abstract class FogRendererMixin {
    //#if MC>=260000
    //$$ @org.spongepowered.asm.mixin.injection.Inject(method = "computeFogColor", at = @At("RETURN"))
    //$$ private void modifyColor(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci, @Local(argsOnly = true) Vector4f original) {
    //$$
    //$$     String color = FuzzSettings.fogColor;
    //$$     if (!HexValidator.isValidHexColor(color)) return;
    //$$     int rgb = Integer.parseInt(color.substring(1), 16);
    //$$     original.set(((rgb >>> 16) & 255) / 255.0F, ((rgb >>> 8) & 255) / 255.0F, (rgb & 255) / 255.0F, original.w);
    //$$ }
    //#elseif MC>=12106
    @ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
    private Vector4f modifyColor(Vector4f original) {

        String color = FuzzSettings.fogColor;
        if (!HexValidator.isValidHexColor(color)) return original;
        int rgb = Integer.parseInt(color.substring(1), 16);
        return new Vector4f(((rgb >>> 16) & 255) / 255.0F, ((rgb >>> 8) & 255) / 255.0F, (rgb & 255) / 255.0F, original.w);
    }
    //#else
    //$$ @ModifyReturnValue(method = "getFogColor", at = @At("RETURN"))
    //$$ private int modifyColor(int original) {
    //$$     String color = FuzzSettings.fogColor;
    //$$     return HexValidator.isValidHexColor(color) ? Integer.parseInt(color.substring(1), 16) : original;
    //$$ }
    //#endif
}
