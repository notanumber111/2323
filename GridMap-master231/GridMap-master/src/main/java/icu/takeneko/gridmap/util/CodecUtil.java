package icu.takeneko.gridmap.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;
import java.util.UUID;

public class CodecUtil {
    public static final Codec<UUID> UUID = Codec.LONG.listOf().flatXmap(
        it -> {
            if (it.size() < 2) {
                return DataResult.error(() -> "Expected 2 elements in list, but got %d!".formatted(it.size()));
            }
            long first = it.get(0);
            long second = it.get(1);
            return DataResult.success(new UUID(first, second));
        },
        it -> DataResult.success(List.of(it.getMostSignificantBits(), it.getLeastSignificantBits()))
    );
}
