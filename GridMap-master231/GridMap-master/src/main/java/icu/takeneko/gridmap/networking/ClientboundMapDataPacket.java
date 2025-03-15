package icu.takeneko.gridmap.networking;

import icu.takeneko.gridmap.GridMap;
import icu.takeneko.gridmap.screen.MapEditScreen;
import icu.takeneko.gridmap.map.MapData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ClientboundMapDataPacket implements Packet {
    public static final ResourceLocation ID = GridMap.of("map_data");
    private final boolean openScreen;
    private final List<MapData> data;

    public ClientboundMapDataPacket(boolean openScreen, List<MapData> data) {
        this.openScreen = openScreen;
        this.data = data;
    }

    public ClientboundMapDataPacket(FriendlyByteBuf buf) {
        this.openScreen = buf.readBoolean();
        this.data = buf.readList(MapData::readFromNetwork);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.openScreen);
        buf.writeCollection(data, MapData::writeToNetwork);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (openScreen) {
            Minecraft.getInstance().setScreen(new MapEditScreen(data));
        } else {
            if (Minecraft.getInstance().screen instanceof MapEditScreen mapEditScreen){
                mapEditScreen.update(data);
            }
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
