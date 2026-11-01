/*
 * This file is part of the Fuzz project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 1024_byteeeee and contributors
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

package top.byteeeee.fuzz.utils.compat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;


@SuppressWarnings("unused")
public class ClientPatterns {
    @PreprocessPattern
    private static Inventory inventory(Player player) {
        //#if MC>=11700
        return player.getInventory();
        //#else
        //$$ return player.inventory;
        //#endif
    }

    @PreprocessPattern
    private static int selectedSlot(Inventory inventory) {
        //#if MC>=12105
        return inventory.getSelectedSlot();
        //#else
        //$$ return inventory.selected;
        //#endif
    }

    @PreprocessPattern
    private static void selectedSlot(Inventory inventory, int slot) {
        //#if MC>=12105
        inventory.setSelectedSlot(slot);
        //#else
        //$$ inventory.selected = slot;
        //#endif
    }

    @PreprocessPattern
    private static MutableComponent literal(String text) {
        //#if MC>=11900
        return Component.literal(text);
        //#else
        //$$ return new net.minecraft.network.chat.TextComponent(text);
        //#endif
    }

    @PreprocessPattern
    private static MutableComponent translatable(String key, Object... args) {
        //#if MC>=11900
        return Component.translatable(key, args);
        //#else
        //$$ return new net.minecraft.network.chat.TranslatableComponent(key, args);
        //#endif
    }

    @PreprocessPattern
    private static Level level(Entity entity) {
        //#if MC>=12000
        return entity.level();
        //#else
        //$$ return entity.level;
        //#endif
    }

    @PreprocessPattern
    private static Abilities abilities(Player player) {
        //#if MC>=11700
        return player.getAbilities();
        //#else
        //$$ return player.abilities;
        //#endif
    }

    @PreprocessPattern
    private static void sendSystemMessage(LocalPlayer player, Component message) {
        //#if MC>=260000
        //$$ player.sendSystemMessage(message);
        //#else
        player.displayClientMessage(message, false);
        //#endif
    }

    @PreprocessPattern
    private static void sendOverlayMessage(LocalPlayer player, Component message) {
        //#if MC>=260000
        //$$ player.sendOverlayMessage(message);
        //#else
        player.displayClientMessage(message, true);
        //#endif
    }
}
