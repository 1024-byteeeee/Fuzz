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

package top.byteeeee.fuzz.commands.rule.commandCoordCompass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.world.phys.Vec3;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.commands.AbstractRuleCommand;
import top.byteeeee.fuzz.renderer.rule.commandCoordCompass.CoordCompassRenderer;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.Layout;
import top.byteeeee.fuzz.utils.Messenger;

@Environment(EnvType.CLIENT)
public class CoordCompassCommand extends AbstractRuleCommand {
    private static final CoordCompassCommand INSTANCE = new CoordCompassCommand();
    private static final Translator tr = new Translator("command.coordCompass");
    private static final String MAIN_CMD_NAME = "coordCompass";
    private static final String RULE_NAME = "commandCoordCompass";

    public static CoordCompassCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
        ClientCommands.literal(MAIN_CMD_NAME)
        .then(ClientCommands.literal("set")
        .then(ClientCommands.argument("x", DoubleArgumentType.doubleArg())
        .then(ClientCommands.argument("y", DoubleArgumentType.doubleArg())
        .then(ClientCommands.argument("z", DoubleArgumentType.doubleArg())
        .executes(c -> checkEnabled(c, () -> set(c)))))))
        .then(ClientCommands.literal("clear")
        .executes(c -> checkEnabled(c, CoordCompassCommand::clear)))
        .then(ClientCommands.literal("help").executes(c -> checkEnabled(c, () -> help(c)))));
    }

    @Override
    protected boolean getCondition() {
        return FuzzSettings.commandCoordCompass;
    }

    @Override
    protected String getRuleName() {
        return RULE_NAME;
    }

    private static int set(CommandContext<FabricClientCommandSource> ctx) {
        double x = DoubleArgumentType.getDouble(ctx, "x");
        double y = DoubleArgumentType.getDouble(ctx, "y");
        double z = DoubleArgumentType.getDouble(ctx, "z");
        CoordCompassRenderer.targetCoord = new Vec3(x, y, z);
        CoordCompassRenderer.isActive = true;
        return 1;
    }

    private static int clear() {
        CoordCompassRenderer.targetCoord = null;
        CoordCompassRenderer.isActive = false;
        return 1;
    }

    private static int help(CommandContext<FabricClientCommandSource> ctx) {
        Messenger.tell(ctx.getSource(), Messenger.f(Messenger.c(
            tr.tr("help.set"), Messenger.endl(),
            tr.tr("help.clear"), Messenger.endl(),
            tr.tr("help.help"), Messenger.endl()
        ), Layout.GRAY));

        return 1;
    }
}
