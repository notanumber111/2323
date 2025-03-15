package icu.takeneko.gridmap.map.elements;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.gridmap.map.MapElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class TextElement extends MapElement {
    public static final MapCodec<TextElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
        Codec.STRING.fieldOf("text").forGetter(o -> o.text),
        Codec.FLOAT.fieldOf("x").forGetter(o -> o.x),
        Codec.FLOAT.fieldOf("y").forGetter(o -> o.y)
    ).apply(ins, TextElement::new));

    private String text;
    private float x;
    private float y;

    @Override
    protected MapCodec<? extends MapElement> codec() {
        return CODEC;
    }
}
