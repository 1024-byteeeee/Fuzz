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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import top.byteeeee.fuzz.utils.AutoMixinAuditExecutor;

public class FuzzMod implements ModInitializer {
    private static String version;
	private static final FuzzMod INSTANCE = new FuzzMod();

	public static FuzzMod getInstance() {
		return INSTANCE;
	}

    public String getVersion() {
        return version;
    }

    @Override
	public void onInitialize() {
		//#if MC<12106
		AutoMixinAuditExecutor.run();
		//#endif
	}

	public void onMinecraftClientInit() {
        version = FabricLoader.getInstance().getModContainer(FuzzModClient.MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        //#if MC>=12106
		//$$ AutoMixinAuditExecutor.run();
		//#endif
	}
}
