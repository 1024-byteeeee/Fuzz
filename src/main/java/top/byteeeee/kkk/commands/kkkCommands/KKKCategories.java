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

package top.byteeeee.kkk.commands.kkkCommands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import net.minecraft.text.BaseText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import top.byteeeee.kkk.KKKModClient;
import top.byteeeee.kkk.KKKSettings;
import top.byteeeee.kkk.settings.KKKFunction;
import top.byteeeee.kkk.translations.LanguageJudge;
import top.byteeeee.kkk.translations.Translator;
import top.byteeeee.kkk.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.kkk.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.kkk.utils.Messenger;

import java.lang.reflect.Field;
import java.util.*;

@Environment(EnvType.CLIENT)
public class KKKCategories {
    protected static final Translator tr = new Translator("categories");

    protected static MutableText showCategories() {
        Set<String> categories = getAllCategories();
        BaseText categoryButtons = Messenger.s("");
        for (String category : categories) {
            MutableText hoverText;
            if (LanguageJudge.isEnglish()) {
                hoverText = tr.tr("click_to_view", tr.tr(category)).formatted(Formatting.YELLOW);
            } else {
                hoverText = tr.tr("click_to_view", tr.tr(category), category).formatted(Formatting.YELLOW);
            }
            categoryButtons.append(
            Messenger.s("[" + tr.tr(category).getString() + "]")
            .styled(style -> style
            .withColor(Formatting.AQUA)
            .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, "/kkk list " + category))
            .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, hoverText)))).append(" ");
        }
        return categoryButtons;
    }

    protected static Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Field field : KKKSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(KKKFunction.class)) {
                categories.addAll(Arrays.asList(field.getAnnotation(KKKFunction.class).categories()));
            }
        }
        return categories;
    }

    protected static int showFunctionListByCategory(FabricClientCommandSource source, String category) {
        List<MutableText> messages = new ArrayList<>();
        messages.add(tr.tr("list_title_for_category", tr.tr(category)).formatted(Formatting.AQUA, Formatting.BOLD));

        Arrays.stream(KKKSettings.class.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(KKKFunction.class))
            .filter(f -> Arrays.asList(f.getAnnotation(KKKFunction.class).categories()).contains(category))
            .forEach(field -> {
                try {
                    Object value = field.get(null);
                    messages.add(KKKCommandContext.functionEntryText(field, value));
                } catch (IllegalAccessException e) {
                    KKKModClient.LOGGER.error("Error to access filed {}: {}", field.getName(), e.getMessage());
                }
            });

        messages.forEach(message -> Messenger.tell(source, message));
        return 1;
    }
}
