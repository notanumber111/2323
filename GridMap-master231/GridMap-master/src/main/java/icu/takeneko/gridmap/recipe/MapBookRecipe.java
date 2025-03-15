package icu.takeneko.gridmap.recipe;

import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.all.AllRecipeSerializers;
import icu.takeneko.gridmap.item.GridMapBookItem;
import icu.takeneko.gridmap.item.GridMapItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MapBookRecipe extends CustomRecipe {
    public MapBookRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        int mapCount = 0;
        for (ItemStack it : craftingContainer.getItems()) {
            if (it.isEmpty()) continue;
            if (it.is(AllItems.GRID_MAP.asItem()) && GridMapItem.getUUID(it) != null) {
                mapCount++;
            }
        }
        return mapCount > 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        ItemStack itemStack = AllItems.GRID_MAP_BOOK.asStack();
        return GridMapBookItem.updateGridMapsInItem(
            itemStack,
            craftingContainer.getItems()
                .stream()
                .map(it -> it.is(AllItems.GRID_MAP.asItem()) ? GridMapItem.getUUID(it) : null)
                .filter(Objects::nonNull)
                .toList()
        );
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipeSerializers.MAP_BOOK_CRAFTING;
    }
}
