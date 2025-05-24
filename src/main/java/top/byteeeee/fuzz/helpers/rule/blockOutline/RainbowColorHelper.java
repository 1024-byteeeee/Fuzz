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

package top.byteeeee.fuzz.helpers.rule.blockOutline;

//#if MC>=12102
//$$ import top.byteeeee.fuzz.FuzzSettings;
//#endif

public class RainbowColorHelper {
    public static float[] hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = v - c;

        float r, g, b;

        if (h < 1.0F / 6.0F) {
            r = c; g = x; b = 0;
        } else if (h < 2.0F / 6.0F) {
            r = x; g = c; b = 0;
        } else if (h < 3.0F / 6.0F) {
            r = 0; g = c; b = x;
        } else if (h < 4.0F / 6.0F) {
            r = 0; g = x; b = c;
        } else if (h < 5.0F / 6.0F) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }

        return new float[] { r + m, g + m, b + m };
    }

    //#if MC>=12102
    //$$public static int getRainbowColor() {
    //$$    long time = System.currentTimeMillis();
    //$$    float speed = 3000.0F;
    //$$    float hue = (time % (long) speed) / speed;
    //$$    float[] rgb = RainbowColorHelper.hsvToRgb(hue, 1.0F, 1.0F);
    //$$    int red = (int) (rgb[0] * 255);
    //$$    int green = (int) (rgb[1] * 255);
    //$$    int blue = (int) (rgb[2] * 255);
    //$$    return getArgb(FuzzSettings.blockOutlineAlpha, red, green, blue);
    //$$}
    //#else
    public static float[] getRainbowColorComponents() {
        long time = System.currentTimeMillis();
        float speed = 3000.0F;
        float hue = (time % (long) speed) / speed;
        return RainbowColorHelper.hsvToRgb(hue, 1.0F, 1.0F);
    }
    //#endif

    //#if MC>=12102
    //$$ public static int getArgb(int alpha, int red, int green, int blue) {
    //$$     return alpha << 24 | red << 16 | green << 8 | blue;
    //$$ }
    //#endif
}
