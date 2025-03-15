package icu.takeneko.gridmap.all;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import icu.takeneko.gridmap.GridMap;
import icu.takeneko.gridmap.item.GridMapBookItem;
import icu.takeneko.gridmap.item.GridMapItem;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static icu.takeneko.gridmap.GridMap.REGISTRATE;

public class AllItems {

    public static final ItemEntry<GridMapItem> GRID_MAP = REGISTRATE
        .item("grid_map",GridMapItem::new)
        .defaultLang()
        .defaultModel()
        .initialProperties(() -> new Item.Properties().stacksTo(1))
        .recipe((ctx, prov) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ctx.get().asItem())
                .pattern(" A ")
                .pattern("BCD")
                .pattern(" E ")
                .define('A', Items.MAP)
                .define('B', Items.RED_DYE)
                .define('C', Items.GREEN_DYE)
                .define('D', Items.BLUE_DYE)
                .define('E', Items.FEATHER)
                .unlockedBy("has_item", RegistrateRecipeProvider.has(Items.MAP))
                .save(prov);
            SpecialRecipeBuilder.special(AllRecipeSerializers.GRID_MAP_EXTEND)
                .save(prov, GridMap.MOD_ID + ":grid_map_extend");
        })
        .register();


    public static final ItemEntry<GridMapBookItem> GRID_MAP_BOOK = REGISTRATE
        .item("grid_map_book",GridMapBookItem::new)
        .defaultLang()
        .defaultModel()
        .recipe((ctx, prov) -> {
            SpecialRecipeBuilder.special(AllRecipeSerializers.MAP_BOOK_CRAFTING)
                .save(prov, GridMap.MOD_ID + ":grid_map_book");
        })
        .initialProperties(Item.Properties::new)
        .register();


    public static void register() {
        //intentionally empty
    }
}
