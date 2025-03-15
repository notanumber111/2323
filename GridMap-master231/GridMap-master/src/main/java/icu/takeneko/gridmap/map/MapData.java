package icu.takeneko.gridmap.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.gridmap.util.CodecUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@ToString
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MapData extends SavedData {

    public static final Codec<MapData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
        MapElement.CODEC.listOf().fieldOf("elements").forGetter(MapData::getElements),
        Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(MapData::getDimension),
        CodecUtil.UUID.fieldOf("uuid").forGetter(MapData::getUuid),
        Codec.INT.fieldOf("scale").forGetter(MapData::getScale),
        Codec.BOOL.fieldOf("locked").forGetter(MapData::isLocked),
        Codec.INT.fieldOf("centerChunkX").forGetter(MapData::getCenterChunkX),
        Codec.INT.fieldOf("centerChunkZ").forGetter(MapData::getCenterChunkZ)
    ).apply(ins, MapData::new));

    static {
        MapElement.register();
    }

    private final List<MapElement> elements;
    private final ResourceKey<Level> dimension;
    private final UUID uuid;
    @Setter
    private int scale;
    @Setter
    private boolean locked;
    private final int centerChunkX;
    private final int centerChunkZ;

    public MapData(List<MapElement> elements, ResourceKey<Level> dimension, UUID uuid, int scale, boolean locked, int centerChunkX, int centerChunkZ) {
        this.elements = new ArrayList<>(elements);
        this.dimension = dimension;
        this.uuid = uuid;
        this.scale = scale;
        this.locked = locked;
        this.centerChunkX = centerChunkX;
        this.centerChunkZ = centerChunkZ;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return (CompoundTag) CODEC.encode(this, NbtOps.INSTANCE, compoundTag)
            .getOrThrow(false, log::error);
    }

    public int mapWidthInChunks() {
        return 8 * (1 << scale);
    }

    public static MapData readFromTag(CompoundTag tag) {
        return CODEC.decode(NbtOps.INSTANCE, tag)
            .getOrThrow(false, log::error)
            .getFirst();
    }

    public static MapData readFromNetwork(FriendlyByteBuf buf) {
        return readFromTag(buf.readNbt());
    }

    public static void writeToNetwork(FriendlyByteBuf buf, MapData data) {
        CompoundTag tag = new CompoundTag();
        buf.writeNbt(data.save(tag));
    }

    @Nullable
    public static MapData getData(
        DimensionDataStorage dataStorage,
        UUID uuid
    ) {
        String id = "grid_map_" + uuid;
        return dataStorage.get(MapData::readFromTag, id);
    }

    public static MapData getOrCreateData(
        DimensionDataStorage dataStorage,
        UUID uuid,
        ChunkPos creationChunkPos,
        ResourceKey<Level> dimension
    ) {
        String id = "grid_map_" + uuid.toString();
        return dataStorage.computeIfAbsent(
            MapData::readFromTag,
            () -> {
                MapData created = new MapData(
                    List.of(),
                    dimension,
                    uuid,
                    0,
                    false,
                    creationChunkPos.x,
                    creationChunkPos.z
                );
                created.setDirty();
                return created;
            },
            id
        );
    }
}
