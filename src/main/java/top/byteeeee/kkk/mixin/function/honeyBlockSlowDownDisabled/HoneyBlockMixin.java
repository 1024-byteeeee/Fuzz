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

package top.byteeeee.kkk.mixin.function.honeyBlockSlowDownDisabled;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;

import org.spongepowered.asm.mixin.Mixin;

import top.byteeeee.kkk.KKKSettings;

@Environment(EnvType.CLIENT)
@Mixin(HoneyBlock.class)
public abstract class HoneyBlockMixin extends Block {
    public HoneyBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public float getVelocityMultiplier() {
        return KKKSettings.honeyBlockSlowDownDisabled ? Blocks.GRAY_CONCRETE.getVelocityMultiplier() : super.getVelocityMultiplier();
    }

    @Override
    public float getJumpVelocityMultiplier() {
        return KKKSettings.honeyBlockSlowDownDisabled ? Blocks.GRAY_CONCRETE.getJumpVelocityMultiplier() : super.getJumpVelocityMultiplier();
    }
}
