package icu.takeneko.gridmap.recipe;

import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.all.AllRecipeSerializers;
import icu.takeneko.gridmap.item.GridMapItem;
import icu.takeneko.gridmap.map.MapData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GridMapExtendRecipe extends CustomRecipe {
    public GridMapExtendRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> items = craftingContainer.getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (i == 4 && !item.is(AllItems.GRID_MAP.asItem())) return false;
            if (i != 4 && !item.is(Items.PAPER)) return false;
            if (i == 4 && GridMapItem.getUUID(item) == null) return false;
        }
        System.out.println("matches");
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        ItemStack itemStack = craftingContainer.getItem(4).copyWithCount(1);
        if (GridMapItem.getUUID(itemStack) != null && GridMapItem.getScale(itemStack) < 3) {
            int scale = GridMapItem.getScale(itemStack) + 1;
            MapData mapData = MapData.getData(
                ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage(),
                Objects.requireNonNull(GridMapItem.getUUID(itemStack))
            );
            mapData.setDirty();
            mapData.setScale(scale);
            return GridMapItem.putScale(itemStack, scale).copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i >= 3 && i1 >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AllRecipeSerializers.GRID_MAP_EXTEND;
    }
}
