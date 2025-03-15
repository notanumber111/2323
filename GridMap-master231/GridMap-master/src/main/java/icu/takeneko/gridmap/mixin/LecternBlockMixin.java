package icu.takeneko.gridmap.mixin;

import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.item.GridMapBookItem;
import icu.takeneko.gridmap.item.GridMapItem;
import icu.takeneko.gridmap.map.MapData;
import icu.takeneko.gridmap.networking.ClientboundMapDataPacket;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Slf4j
@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin {
    @Shadow
    @Final
    public static BooleanProperty HAS_BOOK;

    @Shadow
    private static void placeBook(@Nullable Entity pEntity, Level pLevel, BlockPos pPos, BlockState pState, ItemStack pStack) {
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    void handleGridMapUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (pState.getValue(HAS_BOOK) && pLevel.getBlockEntity(pPos) instanceof LecternBlockEntity blockEntity) {
            if (!pLevel.isClientSide) {
                ItemStack itemStack = blockEntity.getBook();
                if (itemStack.is(AllItems.GRID_MAP_BOOK.get())) {
                    List<UUID> uuids = GridMapBookItem.getGridMapsInItem(itemStack);
                    if (!uuids.isEmpty()) {
                        log.info("Requesting open books on server side: {}", uuids);
                        DimensionDataStorage dataStorage = pLevel.getServer().overworld().getDataStorage();
                        AllNetworking.sendToPlayer(
                            (ServerPlayer) pPlayer,
                            new ClientboundMapDataPacket(
                                true,
                                uuids.stream().map(it -> MapData.getOrCreateData(
                                    dataStorage,
                                    it,
                                    new ChunkPos(pPos),
                                    pLevel.dimension()
                                )).toList()
                            )
                        );
                        cir.setReturnValue(InteractionResult.sidedSuccess(true));
                        cir.cancel();
                    }
                }
                if (itemStack.is(AllItems.GRID_MAP.get())) {
                    UUID uuid = GridMapItem.getUUID(itemStack);
                    if (uuid != null) {
                        log.info("Requesting open map on server side: {}", uuid);
                        DimensionDataStorage dataStorage = pLevel.getServer().overworld().getDataStorage();
                        AllNetworking.sendToPlayer(
                            (ServerPlayer) pPlayer,
                            new ClientboundMapDataPacket(
                                true,
                                List.of(MapData.getOrCreateData(
                                    dataStorage,
                                    uuid,
                                    new ChunkPos(pPos),
                                    pLevel.dimension()
                                ))
                            )
                        );
                        cir.setReturnValue(InteractionResult.sidedSuccess(true));
                        cir.cancel();
                    }
                }
            }
            return;
        }
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.is(AllItems.GRID_MAP.get()) || itemStack.is(AllItems.GRID_MAP_BOOK.get())) {
            if (itemStack.is(AllItems.GRID_MAP.get()) && GridMapItem.getUUID(itemStack) == null) {
                return;
            }
            if (itemStack.is(AllItems.GRID_MAP_BOOK.get()) && GridMapBookItem.getGridMapsInItem(itemStack).isEmpty()) {
                return;
            }
            if (!pLevel.isClientSide) {
                placeBook(pPlayer, pLevel, pPos, pState, itemStack);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(true));
            cir.cancel();
        }

    }
}
