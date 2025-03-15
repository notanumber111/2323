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
public class MarkElement extends MapElement {
    public static final MapCodec<MarkElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
        Codec.INT.fieldOf("color").forGetter(MarkElement::getColor),
        Codec.STRING.fieldOf("text").forGetter(MarkElement::getName),
        Codec.FLOAT.fieldOf("x").forGetter(MarkElement::getX),
        Codec.FLOAT.fieldOf("y").forGetter(MarkElement::getY)
    ).apply(ins, MarkElement::new));

    private final int color;
    private final String name;
    private final float x;
    private final float y;

    @Override
    protected MapCodec<? extends MapElement> codec() {
        return CODEC;
    }
}
