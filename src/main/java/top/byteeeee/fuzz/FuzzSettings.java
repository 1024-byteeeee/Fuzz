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

package top.byteeeee.fuzz;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.fuzz.config.FuzzRuleConfig;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.config.FuzzConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import static top.byteeeee.fuzz.settings.RuleCategory.*;

@Environment(EnvType.CLIENT)
public class FuzzSettings {
    public static Map<String, Object> DEFAULT_VALUES = new ConcurrentHashMap<>();
    public static List<String> highlightEntityList = new ArrayList<>();

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean usingItemSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean sneakingSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean hurtShakeDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, RENDER})
    public static boolean renderHandDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean bedRockFlying = false;

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FFFFFF", "#FF88C2"}
    )
    public static String blockOutlineColor = "false";

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"-1", "0", "255"}
    )
    public static int blockOutlineAlpha = -1;

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"-1.0", "0.0", "10.0"}
    )
    public static double blockOutlineWidth = -1.0D;

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String skyColor = "false";

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String fogColor = "false";

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String waterColor = "false";

    @Rule(
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        options = {"false", "#FF88C2"}
    )
    public static String waterFogColor = "false";

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean campfireSmokeParticleDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL, CARPET})
    public static boolean quickKickFakePlayer = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL, CARPET})
    public static boolean quickDropFakePlayerAllItemStack = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL, EXPERIMENTAL})
    public static boolean letFluidInteractLikeAir = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean fluidPushDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean slimeBlockSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean cobwebSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean iceSlipperinessDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean soulSandBlockSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean honeyBlockSlowDownDisabled = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean bubbleColumnInteractDisabled = false;

    @Rule(
        categories = {FUZZ, FEATURE, CREATIVE, QOL},
        options = {"false", "true", "sneaking"}
    )
    public static String pickFluidBucketItemInCreative = "false";

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, COMMAND})
    public static boolean commandHighLightEntities = false;

    static {
        for (Field field : FuzzSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    field.setAccessible(true);
                    Object defaultValue = field.get(null);
                    DEFAULT_VALUES.put(field.getName(), defaultValue);
                } catch (IllegalAccessException e) {
                    FuzzModClient.LOGGER.warn(e);
                }
            }
        }
        FuzzConfig.load();
        FuzzRuleConfig.load();
    }
}
