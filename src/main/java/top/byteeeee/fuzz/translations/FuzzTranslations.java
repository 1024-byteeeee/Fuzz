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

package top.byteeeee.fuzz.translations;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import top.byteeeee.fuzz.FuzzModClient;
import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.utils.FileUtil;
import top.byteeeee.yaml.TinyYamlParser;
import top.byteeeee.yaml.exception.YamlParseException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class FuzzTranslations {
    private static final String LANG_DIR = "assets/" + FuzzModClient.MOD_ID + "/lang";
    private static final Map<String, Map<String, String>> translations = new LinkedHashMap<>();

    public static void loadTranslations() {
        Map<String, Object> yaml = readTranslationYaml(LANG_DIR + "/meta/languages.yml");
        if (yaml == null || !(yaml.get("languages") instanceof List)) {
            return;
        }

        for (String language : TinyYamlParser.getNestedStringList(yaml, "languages")) {
            Map<String, Object> languageYaml = readTranslationYaml(LANG_DIR + "/" + language + ".yml");
            if (languageYaml == null) {
                continue;
            }
            Map<String, String> translation = new LinkedHashMap<>();
            buildTranslationMap(translation, languageYaml);
            translations.put(language, translation);
        }
    }

    @Nullable
    private static Map<String, Object> readTranslationYaml(String path) {
        try {
            return TinyYamlParser.parse(FileUtil.readFile(path));
        } catch (IOException | YamlParseException e) {
            FuzzModClient.LOGGER.warn("Failed to load translation YAML: {}", path, e);
            return null;
        }
    }

    private static void buildTranslationMap(Map<String, String> translation, Map<?, ?> yaml) {
        Deque<YamlFrame> stack = new ArrayDeque<>();
        stack.push(new YamlFrame(yaml, ""));

        while (!stack.isEmpty()) {
            YamlFrame frame = stack.peek();
            if (!frame.entries.hasNext()) {
                stack.pop();
                continue;
            }

            Map.Entry<?, ?> entry = frame.entries.next();
            String key = (String) entry.getKey();
            String fullKey = frame.prefix.isEmpty() ? key : frame.prefix + "." + key;
            Object value = entry.getValue();
            if (value instanceof Map) {
                stack.push(new YamlFrame((Map<?, ?>) value, fullKey));
            } else {
                translation.put(fullKey, Objects.toString(value, ""));
            }
        }
    }

    @NotNull
    public static String getLanguage() {
        String language = Objects.toString(FuzzSettings.language, TranslationConstants.DEFAULT_LANGUAGE).toLowerCase(Locale.ROOT);
        return translations.containsKey(language) ? language : TranslationConstants.DEFAULT_LANGUAGE;
    }

    @Nullable
    public static String translateKeyToFormattedString(String lang, String key) {
        Map<String, String> translation = translations.get(lang);
        String translated = translation == null ? null : translation.get(key);

        if (translated != null || TranslationConstants.DEFAULT_LANGUAGE.equals(lang)) {
            return translated;
        }

        Map<String, String> fallback = translations.get(TranslationConstants.DEFAULT_LANGUAGE);
        return fallback == null ? null : fallback.get(key);
    }

    public static boolean isEnglish() {
        return getLanguage().startsWith("en");
    }

    private static final class YamlFrame {
        private final Iterator<? extends Map.Entry<?, ?>> entries;
        private final String prefix;

        private YamlFrame(Map<?, ?> yaml, String prefix) {
            this.entries = yaml.entrySet().iterator();
            this.prefix = prefix;
        }
    }
}
