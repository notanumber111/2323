package icu.takeneko.gridmap.networking;

import icu.takeneko.gridmap.map.MapData;
import icu.takeneko.gridmap.map.MapElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerboundRemoveComponentPacket implements Packet {

    private final MapElement element;
    private final UUID uuid;

    public ServerboundRemoveComponentPacket(MapElement element, UUID uuid) {
        this.element = element;
        this.uuid = uuid;
    }

    public ServerboundRemoveComponentPacket(FriendlyByteBuf buf){
        this.element = MapElement.CODEC.decode(NbtOps.INSTANCE,buf.readNbt())
            .getOrThrow(false, System.out::println).getFirst();
        this.uuid = buf.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag)MapElement.CODEC.encodeStart(NbtOps.INSTANCE, element)
            .getOrThrow(false, System.out::println)
        );
        buf.writeUUID(uuid);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        MinecraftServer server = player.server;
        MapData data = MapData.getOrCreateData(
            server.overworld().getDataStorage(),
            uuid,
            player.chunkPosition(),
            player.level().dimension()
        );
        data.setDirty();
        data.getElements().remove(element);
    }
}
