/*
 * This file is part of the ImGui Minecraft project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  ZhuRuoLing and contributors
 *
 * ImGui Minecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImGui Minecraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ImGui Minecraft.  If not, see <https://www.gnu.org/licenses/>.
 */

package icu.takeneko.gridmap.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import icu.takeneko.gridmap.MCImGui;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Shadow
    @Final
    private long window;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onWindowCreate(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci) {
        ImGui.init();
        ImGui.createContext();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
        MCImGui.IMGUI_GLFW.init(this.window, true);
        MCImGui.IMGUI_GL3.init(MCImGui.GLSL_VERSION);
    }

    @Inject(method = "close", at = @At("HEAD"))
    void onWindowDispose(CallbackInfo ci) {
        MCImGui.IMGUI_GL3.dispose();
        MCImGui.IMGUI_GLFW.dispose();
        ImGui.destroyContext();
    }
}
