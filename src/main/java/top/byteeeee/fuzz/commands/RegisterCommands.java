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

package top.byteeeee.fuzz.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//#if MC<11900
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
//#else
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#endif
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import top.byteeeee.fuzz.commands.rule.commandHighLightEntity.HighLightEntityCommand;
import top.byteeeee.fuzz.commands.fuzzCommands.FuzzCommand;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class RegisterCommands {
    public static void register() {
        registerCommand(FuzzCommand::register);
        registerCommand(HighLightEntityCommand::register);
    }

    private static void registerCommand(Consumer<CommandDispatcher<FabricClientCommandSource>> registrator) {
        //#if MC<11900
        registrator.accept(ClientCommandManager.DISPATCHER);
        //#else
        //$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registrator.accept(dispatcher));
        //#endif
    }
}
