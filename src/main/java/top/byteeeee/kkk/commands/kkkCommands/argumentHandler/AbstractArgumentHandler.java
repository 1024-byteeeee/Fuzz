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

package top.byteeeee.kkk.commands.kkkCommands.argumentHandler;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.kkk.settings.KKKFunction;
import top.byteeeee.kkk.config.KKKConfig;
import top.byteeeee.kkk.commands.kkkCommands.KKKCommandContext;
import top.byteeeee.kkk.translations.LanguageJudge;
import top.byteeeee.kkk.translations.Translator;
import top.byteeeee.kkk.utils.Messenger;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public abstract class AbstractArgumentHandler<T> implements ArgumentHandlerInterface<T> {
    protected static final Translator tr = new Translator("command");
    protected Field currentField;

    @Override
    public void configureArgument(LiteralArgumentBuilder<FabricClientCommandSource> literal, Field field) {
        this.currentField = field;
        RequiredArgumentBuilder<FabricClientCommandSource, T> valueArg = ClientCommandManager
        .argument("value", getArgumentType())
        .suggests(this::getSuggestions)
        .executes(ctx -> executeSetValue(ctx, field));
        literal.executes(
            ctx -> KKKCommandContext.showFunctionInfo(ctx.getSource(), field)
        ).then(valueArg);
    }

    protected void setFieldValue(Field field, T value) {
        try {
            field.set(null, value);
            KKKConfig.saveConfig();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set field value", e);
        }
    }

    protected String[] getAnnotationOptions() {
        KKKFunction annotation = this.currentField.getAnnotation(KKKFunction.class);
        return annotation != null ? annotation.options() : new String[0];
    }

    protected void buildCommonCommand(LiteralArgumentBuilder<FabricClientCommandSource> literal, Field field) {
        literal.executes(ctx -> KKKCommandContext.showFunctionInfo(ctx.getSource(), field))
        .then(ClientCommandManager.argument("value", getArgumentType())
        .suggests(this::getSuggestions)
        .executes(ctx -> executeSetValue(ctx, field)));
    }

    private int executeSetValue(CommandContext<FabricClientCommandSource> ctx, Field field) throws CommandSyntaxException {
        T value = parseValue(ctx);
        setFieldValue(field, value);
        String funcNameTrKey = tr.getFuncNameTrKey(field.getName());
        if (LanguageJudge.isEnglish()) {
            Messenger.tell(ctx.getSource(), tr.tr("set_value", field.getName(), value));
        } else {
            Messenger.tell(
                ctx.getSource(),
                tr.tr("set_value", Messenger.tr(funcNameTrKey), field.getName(), value)
            );
        }
        return 1;
    }

    protected abstract ArgumentType<T> getArgumentType();
}
