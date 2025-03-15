package icu.takeneko.gridmap.map.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.gridmap.map.MapElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RectElement extends MapElement {
    public static final MapCodec<RectElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
        Codec.INT.fieldOf("fillColor").forGetter(o -> o.fillColor),
        Codec.FLOAT.fieldOf("x1").forGetter(RectElement::getX1),
        Codec.FLOAT.fieldOf("y1").forGetter(RectElement::getY1),
        Codec.FLOAT.fieldOf("x2").forGetter(RectElement::getX2),
        Codec.FLOAT.fieldOf("y2").forGetter(RectElement::getY2)
    ).apply(ins, RectElement::new));


    private final int fillColor;
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    @Override
    protected MapCodec<? extends MapElement> codec() {
        return CODEC;
    }

    @Override
    public float getX() {
        return x1;
    }

    @Override
    public float getY() {
        return y1;
    }
}
