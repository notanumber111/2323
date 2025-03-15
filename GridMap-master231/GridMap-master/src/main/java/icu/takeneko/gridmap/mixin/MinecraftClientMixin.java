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

import com.mojang.blaze3d.platform.Window;
import icu.takeneko.gridmap.MCImGui;
import icu.takeneko.gridmap.screen.ImApp;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static imgui.ImGui.*;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Nullable
    public Screen screen;

    @Inject(
        method = "runTick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V",
            ordinal = 0
        )
    )
    void onNewFrame(boolean bl, CallbackInfo ci) {
        MCImGui.IMGUI_GLFW.newFrame();
        newFrame();
    }

    @Inject(
        method = "runTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V",
            shift = At.Shift.AFTER
        )
    )
    void onRender(boolean bl, CallbackInfo ci) {
        if (this.screen instanceof ImApp app) {
            app.render(window);
        }
        render();
        MCImGui.IMGUI_GL3.renderDrawData(getDrawData());
    }
}
