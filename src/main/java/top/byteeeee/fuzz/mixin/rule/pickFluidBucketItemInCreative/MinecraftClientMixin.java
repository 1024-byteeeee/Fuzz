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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.ClientUtil;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void doItemPick(CallbackInfo ci) {
        if (!Objects.equals(FuzzSettings.pickFluidBucketItemInCreative, "false")) {
            boolean needSneaking = Objects.equals(FuzzSettings.pickFluidBucketItemInCreative, "sneaking");
            MinecraftClient client = ClientUtil.getCurrentClient();
            PlayerEntity player = ClientUtil.getCurrentPlayer();
            World world = ClientUtil.getCurrentPlayer().getEntityWorld();
            Entity cameraEntity = ClientUtil.getCurrentClient().getCameraEntity();
            HitResult crosshairTargetFluid = null;

            if (cameraEntity != null) {
                crosshairTargetFluid = cameraEntity.raycast(16.88F,0.0F,true);
            }

            if (crosshairTargetFluid != null && player.isCreative() && (!needSneaking || player.isSneaking())) {
                //#if MC>=11700
                //$$ PlayerInventory playerInv = player.getInventory();
                //#else
                PlayerInventory playerInv = player.inventory;
                //#endif
                final HitResult.Type type = crosshairTargetFluid.getType();
                if (type.equals(HitResult.Type.BLOCK)) {
                    BlockPos blockPos = ((BlockHitResult) crosshairTargetFluid).getBlockPos();

                    FluidState fluidState;
                    if (world != null) {
                        fluidState = world.getFluidState(blockPos);
                    } else {
                        fluidState = null;
                    }

                    BlockState blockState;
                    if (fluidState != null) {
                        blockState = fluidState.getBlockState();
                    } else {
                        blockState = null;
                    }

                    Runnable pickFluidBucket = () -> {
                        Item targetItem = blockState.getBlock().equals(Blocks.WATER) ? Items.WATER_BUCKET : Items.LAVA_BUCKET;
                        int foundSlot = -1;

                        for (int i = 0; i < playerInv.size(); i++) {
                            if (playerInv.getStack(i).getItem().equals(targetItem)) {
                                foundSlot = i;
                            }
                        }

                        if (foundSlot != -1) {
                            if (PlayerInventory.isValidHotbarIndex(foundSlot)) {
                                //#if MC>=12105
                                //$$ playerInv.setSelectedSlot(foundSlot);
                                //#else
                                playerInv.selectedSlot = foundSlot;
                                //#endif
                            } else {
                                playerInv.swapSlotWithHotbar(foundSlot);
                            }
                        } else {
                            ItemStack fluidBucket = new ItemStack(targetItem);
                            //#if MC>=12105
                            //$$ playerInv.setStack(playerInv.getSelectedSlot(), fluidBucket);
                            //#else
                            playerInv.setStack(playerInv.selectedSlot, fluidBucket);
                            //#endif
                            ClientPlayerInteractionManager interactionManager = client.interactionManager;
                            if (interactionManager != null) {
                                interactionManager.clickCreativeStack(
                                    player.getStackInHand(Hand.MAIN_HAND),
                                    //#if MC>=12105
                                    //$$ 36 + playerInv.getSelectedSlot()
                                    //#else
                                    36 + playerInv.selectedSlot
                                    //#endif
                                );
                            }
                        }

                        ci.cancel();
                    };

                    if (blockState != null && (blockState.getBlock().equals(Blocks.WATER) || blockState.getBlock().equals(Blocks.LAVA))) {
                        pickFluidBucket.run();
                    }
                }
            }
        }
    }
}
