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
public class CheckboxElement extends MapElement {
    public static final MapCodec<CheckboxElement> CODEC = RecordCodecBuilder.mapCodec(ins ->
        ins.group(
            Codec.BOOL.fieldOf("checked").forGetter(CheckboxElement::isChecked),
            Codec.STRING.fieldOf("text").forGetter(CheckboxElement::getText),
            Codec.FLOAT.fieldOf("x").forGetter(CheckboxElement::getX),
            Codec.FLOAT.fieldOf("y").forGetter(CheckboxElement::getY)
        ).apply(ins, CheckboxElement::new)
    );

    private boolean checked;
    private String text;
    private float x;
    private float y;

    @Override
    protected MapCodec<? extends MapElement> codec() {
        return CODEC;
    }
}
