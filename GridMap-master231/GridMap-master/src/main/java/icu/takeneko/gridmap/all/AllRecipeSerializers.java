package icu.takeneko.gridmap.all;

import icu.takeneko.gridmap.recipe.GridMapExtendRecipe;
import icu.takeneko.gridmap.recipe.MapBookRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class AllRecipeSerializers {
    public static final RecipeSerializer<? extends CraftingRecipe> MAP_BOOK_CRAFTING = new SimpleCraftingRecipeSerializer<>(MapBookRecipe::new);
    public static final RecipeSerializer<? extends CraftingRecipe> GRID_MAP_EXTEND = new SimpleCraftingRecipeSerializer<>(GridMapExtendRecipe::new);
}
