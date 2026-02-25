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

package top.byteeeee.fuzz.mixin.rule.pickFluidBucketItemInCreative;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;
import top.byteeeee.fuzz.utils.EntityUtil;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void doItemPick(CallbackInfo ci) {
        if (!Objects.equals(FuzzSettings.pickFluidBucketItemInCreative, "false")) {
            boolean needSneaking = Objects.equals(FuzzSettings.pickFluidBucketItemInCreative, "sneaking");
            Minecraft client = ClientUtil.getCurrentClient();
            Player player = ClientUtil.getCurrentPlayer();
            Level world = EntityUtil.getEntityWorld(ClientUtil.getCurrentPlayer());
            Entity cameraEntity = ClientUtil.getCurrentClient().getCameraEntity();
            HitResult crosshairTargetFluid = null;

            if (cameraEntity != null) {
                crosshairTargetFluid = cameraEntity.pick(16.88F,0.0F,true);
            }

            if (crosshairTargetFluid != null && player.isCreative() && (!needSneaking || player.isShiftKeyDown())) {
                Inventory playerInv = player.getInventory();
                final HitResult.Type type = crosshairTargetFluid.getType();
                if (type.equals(HitResult.Type.BLOCK)) {
                    BlockPos blockPos = ((BlockHitResult) crosshairTargetFluid).getBlockPos();

                    FluidState fluidState;
                    fluidState = world.getFluidState(blockPos);

                    BlockState blockState;
                    blockState = fluidState.createLegacyBlock();

                    Runnable pickFluidBucket = () -> {
                        Item targetItem = blockState.getBlock().equals(Blocks.WATER) ? Items.WATER_BUCKET : Items.LAVA_BUCKET;
                        int foundSlot = -1;

                        for (int i = 0; i < playerInv.getContainerSize(); i++) {
                            if (playerInv.getItem(i).getItem().equals(targetItem)) {
                                foundSlot = i;
                            }
                        }

                        if (foundSlot != -1) {
                            if (Inventory.isHotbarSlot(foundSlot)) {
                                playerInv.setSelectedSlot(foundSlot);
                            } else {
                                playerInv.pickSlot(foundSlot);
                            }
                        } else {
                            ItemStack fluidBucket = new ItemStack(targetItem);
                            playerInv.setItem(playerInv.getSelectedSlot(), fluidBucket);
                            MultiPlayerGameMode interactionManager = client.gameMode;
                            if (interactionManager != null) {
                                interactionManager.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), 36 + playerInv.getSelectedSlot());
                            }
                        }

                        ci.cancel();
                    };

                    if (blockState.getBlock().equals(Blocks.WATER) || blockState.getBlock().equals(Blocks.LAVA)) {
                        pickFluidBucket.run();
                    }
                }
            }
        }
    }
}
