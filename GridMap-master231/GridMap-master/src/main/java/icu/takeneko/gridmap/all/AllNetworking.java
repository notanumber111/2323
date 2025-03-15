package icu.takeneko.gridmap.all;

import icu.takeneko.gridmap.GridMap;
import icu.takeneko.gridmap.networking.ClientboundMapDataPacket;
import icu.takeneko.gridmap.networking.Packet;
import icu.takeneko.gridmap.networking.ServerboundAddComponentPacket;
import icu.takeneko.gridmap.networking.ServerboundRequestOpenMapPacket;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public class AllNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        GridMap.of("main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(
            id++,
            ServerboundRequestOpenMapPacket.class,
            ServerboundRequestOpenMapPacket::encode,
            ServerboundRequestOpenMapPacket::new,
            wrapEnqueue(ServerboundRequestOpenMapPacket::handle)
        );

        INSTANCE.registerMessage(
            id++,
            ClientboundMapDataPacket.class,
            ClientboundMapDataPacket::encode,
            ClientboundMapDataPacket::new,
            wrapEnqueue(ClientboundMapDataPacket::handle)
        );

        INSTANCE.registerMessage(
            id++,
            ServerboundAddComponentPacket.class,
            ServerboundAddComponentPacket::encode,
            ServerboundAddComponentPacket::new,
            wrapEnqueue(ServerboundAddComponentPacket::handle)
        );
        log.info("Registered {} messages.", id);
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> wrapEnqueue(BiConsumer<T, Supplier<NetworkEvent.Context>> original) {
        return (t, sup) -> sup.get().enqueueWork(() -> original.accept(t, sup));
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }

    public static <T extends Packet> void sendToPlayer(ServerPlayer player, T packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static <T extends Packet> void sendToPlayersTrackingChunk(LevelChunk chunk, T packet) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

}
