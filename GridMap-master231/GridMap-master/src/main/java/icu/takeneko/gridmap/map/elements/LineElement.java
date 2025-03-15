package icu.takeneko.gridmap.map.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.gridmap.map.MapElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class LineElement extends MapElement {
    public static final MapCodec<LineElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
        Codec.INT.fieldOf("color").forGetter(LineElement::getColor),
        Codec.FLOAT.fieldOf("x1").forGetter(LineElement::getX1),
        Codec.FLOAT.fieldOf("y1").forGetter(LineElement::getY1),
        Codec.FLOAT.fieldOf("x2").forGetter(LineElement::getX2),
        Codec.FLOAT.fieldOf("y2").forGetter(LineElement::getY2)
    ).apply(ins, LineElement::new));


    private int color;
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
