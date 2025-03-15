package icu.takeneko.gridmap;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import icu.takeneko.gridmap.all.AllCreativeModeTabs;
import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.all.AllRecipeSerializers;
import icu.takeneko.gridmap.data.Lang;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(value = GridMap.MOD_ID)
public class GridMap {
    public static final String MOD_ID = "peacecraftgridmap";
    public static final MyRegistrate REGISTRATE = new MyRegistrate(MOD_ID);

    public static final ImGuiImplGlfw IMGUI_GLFW = new ImGuiImplGlfw();
    public static final ImGuiImplGl3 IMGUI_GL3 = new ImGuiImplGl3();
    public static final String GLSL_VERSION = "#version 150";

    public GridMap() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AllItems.register();
        AllNetworking.register();
        AllCreativeModeTabs.register();
        modEventBus.addListener(GridMap::registerRecipeSerializer);
        REGISTRATE.addDataGenerator(ProviderType.LANG, Lang::accept);
        REGISTRATE.registerEventListeners(modEventBus);
    }

    public static void registerRecipeSerializer(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            event.register(
                ForgeRegistries.Keys.RECIPE_SERIALIZERS,
                of("grid_map_book_crafting"),
                () -> AllRecipeSerializers.MAP_BOOK_CRAFTING
            );
            event.register(
                ForgeRegistries.Keys.RECIPE_SERIALIZERS,
                of("grid_map_extend"),
                () -> AllRecipeSerializers.GRID_MAP_EXTEND
            );
        }
    }

    public static ResourceLocation of(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public static class MyRegistrate extends Registrate {

        protected MyRegistrate(String modid) {
            super(modid);
        }

        @Override
        public Registrate registerEventListeners(IEventBus bus) {
            return super.registerEventListeners(bus);
        }
    }
}
