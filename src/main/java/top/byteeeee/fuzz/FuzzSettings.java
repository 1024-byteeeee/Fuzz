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

package top.byteeeee.fuzz;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import top.byteeeee.fuzz.config.FuzzRuleConfig;
import top.byteeeee.fuzz.observers.rule.fuzzCommandAlias.FuzzCommandAliasObserver;
import top.byteeeee.fuzz.settings.ObserverManager;
import top.byteeeee.fuzz.validators.rule.fuzzCommandAlias.FuzzCommandAliasValidator;
import top.byteeeee.fuzz.settings.Rule;
import top.byteeeee.fuzz.config.FuzzConfig;
import top.byteeeee.fuzz.settings.ValidatorManager;
import top.byteeeee.fuzz.validators.rule.BiomeColor.BiomeColorValidator;
import top.byteeeee.fuzz.validators.rule.blockOutlineAlpha.BlockOutlineAlphaValidator;
import top.byteeeee.fuzz.validators.rule.blockOutlineColor.BlockOutlineColorValidator;
import top.byteeeee.fuzz.validators.rule.blockOutlineWidth.BlockOutlineWidthValidator;
import top.byteeeee.fuzz.validators.rule.rainbowBlockoutlineBlinkSpeed.RainbowBlockOutlineBlinkSpeedValidator;

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

    @Rule(
        options = {"none", "en_us", "zh_cn"},
        categories = {FUZZ, QOL}
    )
    public static String language = "none";

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
        options = {"false", "rainbow", "#FFFFFF", "#FF88C2"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BlockOutlineColorValidator.class,
        strict = false
    )
    public static String blockOutlineColor = "false";

    @Rule(
        options = {"0.1024", "1.024", "5.20", "10.0"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = RainbowBlockOutlineBlinkSpeedValidator.class,
        strict = false
    )
    public static double rainbowBlockOutlineBlinkSpeed = 1.024D;

    @Rule(
        options = {"-1", "0", "255"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BlockOutlineAlphaValidator.class,
        strict = false
    )
    public static int blockOutlineAlpha = -1;

    @Rule(
        options = {"-1.0", "0.0", "10.0"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BlockOutlineWidthValidator.class,
        strict = false
    )
    public static double blockOutlineWidth = -1.0D;

    @Rule(
        options = {"false", "#FF88C2"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BiomeColorValidator.class,
        strict = false
    )
    public static String skyColor = "false";

    @Rule(
        options = {"false", "#FF88C2"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BiomeColorValidator.class,
        strict = false
    )
    public static String fogColor = "false";

    @Rule(
        options = {"false", "#FF88C2"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BiomeColorValidator.class,
        strict = false
    )
    public static String waterColor = "false";

    @Rule(
        options = {"false", "#FF88C2"},
        categories = {FUZZ, FEATURE, SURVIVAL, RENDER},
        validators = BiomeColorValidator.class,
        strict = false
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
        options = {"false", "true", "sneaking"},
        categories = {FUZZ, FEATURE, CREATIVE, QOL}
    )
    public static String pickFluidBucketItemInCreative = "false";

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, COMMAND})
    public static boolean commandHighlightEntities = false;

    @Rule(categories = {FUZZ, FEATURE, SURVIVAL, QOL})
    public static boolean jumpDelayDisabled = false;

    @Rule(
        options = "false",
        categories = {FUZZ, COMMAND},
        validators = FuzzCommandAliasValidator.class,
        observers = FuzzCommandAliasObserver.class,
        strict = false
    )
    public static String fuzzCommandAlias = "false";

    @Rule(categories = {FUZZ, RENDER})
    public static boolean fogRenderDisabled = false;

    @Rule(categories = {FUZZ, SURVIVAL, COMMAND})
    public static boolean commandCoordCompass = false;

    @Rule(
        options = {
            "false", "fuzz"
            //#if MC>=12103
            //$$ ,"carpetorgaddition"
            //#endif
        },
        categories = {FUZZ, QOL}
    )
    public static String parseCoordInMessage = "false";

    static {
        for (Field field : FuzzSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    field.setAccessible(true);
                    Object defaultValue = field.get(null);
                    DEFAULT_VALUES.put(field.getName(), defaultValue);
                    ValidatorManager.init(field);
                    ObserverManager.init(field);
                } catch (IllegalAccessException e) {
                    FuzzModClient.LOGGER.warn(e);
                }
            }
        }
        FuzzConfig.load();
        FuzzRuleConfig.load();
    }
}
