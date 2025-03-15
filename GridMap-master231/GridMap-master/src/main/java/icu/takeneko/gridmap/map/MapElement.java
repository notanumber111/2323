package icu.takeneko.gridmap.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import icu.takeneko.gridmap.map.elements.CheckboxElement;
import icu.takeneko.gridmap.map.elements.MarkElement;
import icu.takeneko.gridmap.map.elements.RectElement;
import icu.takeneko.gridmap.map.elements.TextElement;
import icu.takeneko.gridmap.util.DispatchedCodec;

import java.util.HashMap;
import java.util.Map;

public abstract class MapElement {
    private static final Map<String, MapCodec<? extends MapElement>> CODECS = new HashMap<>();
    private static final Map<MapCodec<? extends MapElement>, String> REVERSED_CODECS = new HashMap<>();

    public static final Codec<MapElement> CODEC = new DispatchedCodec<>(
        k -> (MapCodec<MapElement>) CODECS.get(k),
        it -> (MapCodec<MapElement>) it.codec(),
        ins -> REVERSED_CODECS.get(ins.codec()),
        Codec.STRING,
        "type"
    ).codec();

    public static void register() {
        register("checkbox", CheckboxElement.CODEC);
        register("mark", MarkElement.CODEC);
        register("rect", RectElement.CODEC);
        register("text", TextElement.CODEC);
    }

    private static <T extends MapElement> void register(String id, MapCodec<T> codec) {
        CODECS.put(id, codec);
        REVERSED_CODECS.put(codec, id);
    }


    protected abstract MapCodec<? extends MapElement> codec();

    public abstract float getX();

    public abstract float getY();
}
