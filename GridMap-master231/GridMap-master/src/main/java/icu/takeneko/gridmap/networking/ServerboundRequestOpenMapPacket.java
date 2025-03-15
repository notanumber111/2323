package icu.takeneko.gridmap.networking;

import icu.takeneko.gridmap.GridMap;
import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.map.MapData;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class ServerboundRequestOpenMapPacket implements Packet {
    public static final ResourceLocation ID = GridMap.of("open_map");

    private final List<UUID> uuid;

    public ServerboundRequestOpenMapPacket(List<UUID> uuid) {
        this.uuid = uuid;
    }

    public ServerboundRequestOpenMapPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readList(FriendlyByteBuf::readUUID);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(uuid, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        log.info("Client requesting open map: {}", uuid);
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        MinecraftServer server = player.server;
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();
        List<MapData> mapData = uuid.stream()
            .map(it -> MapData.getOrCreateData(
                dataStorage,
                it,
                player.chunkPosition(),
                player.level().dimension()
            )).toList();
        AllNetworking.sendToPlayer(player, new ClientboundMapDataPacket(true, mapData));
        context.setPacketHandled(true);
    }
}
