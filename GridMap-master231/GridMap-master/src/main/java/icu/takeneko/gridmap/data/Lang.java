package icu.takeneko.gridmap.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class Lang {

    public static void accept(RegistrateLangProvider provider) {
        provider.add("tooltip.peacecraftgridmap.grid_map.scale", "Scale: %d");
        provider.add("tooltip.peacecraftgridmap.grid_map_book", "Contains following maps: ");
        provider.add("itemGroup.peacecraftgridmap.items", "PeaceCraft Grid Map");
        ui(provider, "colorPalette", "Color Palette");
        ui(provider, "tools", "Tools");
        ui(provider, "add_text", "Text");
        ui(provider, "add_line", "Line");
        ui(provider, "add_rect", "Rect");
        ui(provider, "add_checkbox", "Checkbox");
        ui(provider, "pencil", "Pencil");
        ui(provider, "eraser", "Eraser");
        ui(provider, "none", "None");
        ui(provider, "add_checkbox", "Checkbox");
        ui(provider, "askForText", "Label...");


    }

    private static void ui(RegistrateLangProvider provider, String key, String value){
        provider.add("ui.peacecraftgridmap." + key, value);
    }
}
