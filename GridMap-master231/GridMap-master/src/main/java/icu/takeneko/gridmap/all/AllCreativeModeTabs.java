package icu.takeneko.gridmap.all;

import com.tterrag.registrate.util.entry.RegistryEntry;
import icu.takeneko.gridmap.GridMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import static icu.takeneko.gridmap.GridMap.REGISTRATE;

public class AllCreativeModeTabs {
    public static final RegistryEntry<CreativeModeTab> TAB = REGISTRATE
        .defaultCreativeTab(GridMap.MOD_ID, builder -> {
            builder.icon(AllItems.GRID_MAP::asStack)
                .displayItems((gen, output) -> {
                    output.accept(AllItems.GRID_MAP.asStack());
                    output.accept(AllItems.GRID_MAP_BOOK.asStack());
                })
                .title(Component.translatable("itemGroup.peacecraftgridmap.items"))
                .build();
        }).register();

    public static void register() {

    }
}
