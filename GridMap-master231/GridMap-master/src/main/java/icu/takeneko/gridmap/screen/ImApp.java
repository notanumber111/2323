package icu.takeneko.gridmap.screen;

import com.mojang.blaze3d.platform.Window;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static imgui.ImGui.*;

public interface ImApp {
    default void render(Window window) {
        ImGuiViewport viewport = getMainViewport();
        setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
        setNextWindowSize(window.getWidth(), window.getHeight());
        setNextWindowViewport(viewport.getID());
        pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

        int flags = /*ImGuiWindowFlags.NoDocking |*/ ImGuiWindowFlags.NoTitleBar
            | ImGuiWindowFlags.NoCollapse
            | ImGuiWindowFlags.NoMove
            | ImGuiWindowFlags.NoResize
            | ImGuiWindowFlags.NoNavFocus
            | ImGuiWindowFlags.NoBringToFrontOnFocus
            | ImGuiWindowFlags.NoScrollbar
            | ImGuiWindowFlags.NoScrollWithMouse
            | ImGuiWindowFlags.MenuBar;
        begin("MainDockSpace", flags);
        end();
        popStyleVar(3);
    }
}
