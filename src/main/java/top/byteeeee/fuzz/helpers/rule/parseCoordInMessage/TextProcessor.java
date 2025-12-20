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

package top.byteeeee.fuzz.helpers.rule.parseCoordInMessage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import top.byteeeee.fuzz.FuzzSettings;
import top.byteeeee.fuzz.translations.Translator;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.ClickEventUtil;
import top.byteeeee.fuzz.utils.MessageTextEventUtils.HoverEventUtil;
import top.byteeeee.fuzz.utils.Messenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class TextProcessor {
    private static final Pattern COORD_PATTERN = Pattern.compile("(?<=^|\\s|[\\[({])\\s*(-?\\d+(?:\\.\\d+)?)\\s*[,\\s~]+\\s*(-?\\d+(?:\\.\\d+)?)\\s*[,\\s~]+\\s*(-?\\d+(?:\\.\\d+)?)\\s*(?=$|\\s|[])}])");
    private static final Translator tr = new Translator("rule_feedback.parseCoordInMessage");

    public static Component processTextForCoordinates(Component original) {
        List<Component> nodes = new ArrayList<>();
        List<String> parts = new ArrayList<>();
        collectSegments(original, nodes, parts);

        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p);
        String whole = sb.toString();

        Matcher m = COORD_PATTERN.matcher(whole);
        if (!m.find()) return original;
        m.reset();

        List<MatchInfo> matches = new ArrayList<>();
        while (m.find()) {
            matches.add(new MatchInfo(m.start(), m.end(), m.group(1), m.group(2), m.group(3)));
        }

        MutableComponent out = Messenger.s("").setStyle(original.getStyle());
        int cursor = 0;
        int matchIdx = 0;

        for (int i = 0; i < parts.size(); ++i) {
            String segStr = parts.get(i);
            Component segNode = nodes.get(i);
            int segStart = cursor;
            int segEnd = cursor + segStr.length();

            while (matchIdx < matches.size() && matches.get(matchIdx).end <= segStart) matchIdx++;

            int localPos = 0;
            while (matchIdx < matches.size() && matches.get(matchIdx).start < segEnd) {
                MatchInfo mi = matches.get(matchIdx);
                int mStart = mi.start;
                int mEnd = mi.end;

                if (mStart > segStart + localPos) {
                    int len = mStart - (segStart + localPos);
                    out.append(Messenger.s(segStr.substring(localPos, localPos + len)).setStyle(segNode.getStyle()));
                }

                int ovStart = Math.max(segStart, mStart);
                int ovEnd = Math.min(segEnd, mEnd);
                int ovLocalStart = ovStart - segStart;
                int ovLocalEnd = ovEnd - segStart;
                String matchedPiece = segStr.substring(ovLocalStart, ovLocalEnd);
                String fuzzCommand = "/coordCompass set " + mi.x + " " + mi.y + " " + mi.z;
                String orgCommand = "/highlight " + mi.x + " " + mi.y + " " + mi.z + " continue";
                String runCommand = Objects.equals(FuzzSettings.parseCoordInMessage, "fuzz") ? fuzzCommand : orgCommand;
                MutableComponent clickable = Messenger.s(matchedPiece);
                clickable.setStyle(
                    segNode.getStyle().withColor(ChatFormatting.GREEN).withUnderlined(true)
                    .withClickEvent(ClickEventUtil.event(ClickEventUtil.RUN_COMMAND, runCommand))
                    .withHoverEvent(HoverEventUtil.event(HoverEventUtil.SHOW_TEXT, tr.tr("hover_text", mi.x + " " + mi.y + " " + mi.z).withStyle(ChatFormatting.YELLOW)))
                );

                out.append(clickable);
                localPos = ovLocalEnd;

                if (mEnd <= segEnd) {
                    matchIdx++;
                } else {
                    break;
                }
            }

            if (localPos < segStr.length()) {
                out.append(Messenger.s(segStr.substring(localPos)).setStyle(segNode.getStyle()));
            }

            cursor = segEnd;
        }

        return out;
    }

    private static void collectSegments(Component node, List<Component> nodes, List<String> parts) {
        String full = node.getString();
        int childrenLen = 0;

        for (Component child : node.getSiblings()) {
            childrenLen += child.getString().length();
        }

        int ownLen = full.length() - childrenLen;

        if (ownLen > 0) {
            String ownStr = full.substring(0, ownLen);
            nodes.add(node);
            parts.add(ownStr);
        }

        for (Component child : node.getSiblings()) {
            collectSegments(child, nodes, parts);
        }
    }
}
