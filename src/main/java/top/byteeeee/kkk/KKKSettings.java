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

package top.byteeeee.kkk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.kkk.config.KKKFunctionConfig;
import top.byteeeee.kkk.settings.KKKFunction;
import top.byteeeee.kkk.config.KKKConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import static top.byteeeee.kkk.settings.KKKFunctionCategory.*;

@Environment(EnvType.CLIENT)
public class KKKSettings {
    public static Map<String, Object> DEFAULT_VALUES = new ConcurrentHashMap<>();
    public static List<String> highlightEntityList = new ArrayList<>();

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean usingItemSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean sneakingSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean hurtShakeDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, RENDER})
    public static boolean renderHandDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean bedRockFlying = false;

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FFFFFF", "#FF88C2"}
    )
    public static String blockOutlineColor = "false";

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"-1", "0", "255"}
    )
    public static int blockOutlineAlpha = -1;

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String skyColor = "false";

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String fogColor = "false";

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String waterColor = "false";

    @KKKFunction(
        categories = {KKK, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String waterFogColor = "false";

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean campfireSmokeParticleDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL, CARPET})
    public static boolean quickKickFakePlayer = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL, CARPET})
    public static boolean quickDropFakePlayerAllItemStack = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL, EXPERIMENTAL})
    public static boolean letFluidInteractLikeAir = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean fluidPushDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean slimeBlockSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean cobwebSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean iceSlipperinessDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean soulSandBlockSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean honeyBlockSlowDownDisabled = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, QOL})
    public static boolean bubbleColumnInteractDisabled = false;

    @KKKFunction(
        categories = {KKK, FEATURE, CREATIVE, QOL},
        options = {"false", "true", "sneaking"}
    )
    public static String pickFluidBucketItemInCreative = "false";

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL})
    public static boolean highLightWitherSkeletonEntity = false;

    @KKKFunction(categories = {KKK, FEATURE, SURVIVAL, COMMAND})
    public static boolean commandHighLightEntities = false;

    static {
        for (Field field : KKKSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(KKKFunction.class)) {
                try {
                    field.setAccessible(true);
                    Object defaultValue = field.get(null);
                    DEFAULT_VALUES.put(field.getName(), defaultValue);
                } catch (IllegalAccessException e) {
                    KKKModClient.LOGGER.warn(e);
                }
            }
        }
        KKKConfig.load();
        KKKFunctionConfig.load();
    }
}
