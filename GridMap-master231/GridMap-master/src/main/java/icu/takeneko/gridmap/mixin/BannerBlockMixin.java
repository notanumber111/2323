package icu.takeneko.gridmap.mixin;

import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.item.GridMapItem;
import icu.takeneko.gridmap.map.MapData;
import icu.takeneko.gridmap.map.elements.MarkElement;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.BlockHitResult;
import org.codehaus.plexus.util.dag.DAG;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Mixin(BannerBlock.class)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerBlockMixin extends Block {
    public BannerBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.is(AllItems.GRID_MAP.get()) && GridMapItem.getUUID(itemStack) != null) {
            if (pLevel.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            UUID uuid = GridMapItem.getUUID(itemStack);
            DimensionDataStorage dataStorage = pLevel.getServer().overworld().getDataStorage();
            MapData data = MapData.getOrCreateData(
                dataStorage,
                uuid,
                new ChunkPos(pPos),
                pLevel.dimension()
            );
            if (pLevel.getBlockEntity(pPos) instanceof BannerBlockEntity blockEntity) {
                float centerX = data.getCenterChunkX() * 16 + 8f;
                float centerZ = data.getCenterChunkZ() * 16 + 8f;
                Component component = blockEntity.getName();
                data.getElements().add(
                    new MarkElement(
                        blockEntity.getBaseColor().getTextColor(),
                        component == null ? "" : component.getString(),
                        pPos.getX() - centerX,
                        pPos.getZ() - centerZ
                        )
                );
                data.setDirty();
            }
            data.setDirty();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
