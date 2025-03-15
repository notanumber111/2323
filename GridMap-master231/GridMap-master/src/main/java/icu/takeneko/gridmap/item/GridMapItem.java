package icu.takeneko.gridmap.item;

import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.map.MapData;
import icu.takeneko.gridmap.networking.ServerboundRequestOpenMapPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GridMapItem extends Item {
    public GridMapItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack markLocked(ItemStack itemstack) {
        CompoundTag tag = itemstack.getOrCreateTag();
        tag.putBoolean("Locked", true);
        return itemstack.copyWithCount(1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            UUID uuid = getUUID(stack);
            if (uuid != null) {
                AllNetworking.sendToServer(new ServerboundRequestOpenMapPacket(List.of(uuid)));
            }
            return InteractionResultHolder.success(stack);
        }
        if (getUUID(stack) == null) {
            DimensionDataStorage dataStorage = pLevel.getServer().overworld().getDataStorage();
            UUID uuid = createUUID(stack);
            MapData.getOrCreateData(
                dataStorage,
                uuid,
                pPlayer.chunkPosition(),
                pLevel.dimension()
            );
        }
        pPlayer.setItemInHand(pUsedHand, stack.copy());
        return InteractionResultHolder.success(stack);
    }

    public static UUID createUUID(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        UUID uuid = UUID.randomUUID();
        tag.putUUID("MapUUID", uuid);
        return uuid;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return getUUID(pStack) != null;
    }

    @Nullable
    public static UUID getUUID(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (!tag.contains("MapUUID")) return null;
        return tag.getUUID("MapUUID");
    }

    public static int getScale(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("Scale") && tag.get("Scale") instanceof IntTag zoomTag) {
            return Mth.clamp(zoomTag.getAsInt(), 0, 4);
        }
        tag.putInt("Scale", 0);
        return 0;
    }

    public static ItemStack putScale(ItemStack itemStack, int zoomLevel) {
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putInt("Scale", Mth.clamp(zoomLevel, 0, 4));
        return itemStack.copy();
    }

    @Override
    public void appendHoverText(
        ItemStack pStack,
        @Nullable Level pLevel,
        List<Component> pTooltipComponents,
        TooltipFlag pIsAdvanced
    ) {
        UUID uuid = getUUID(pStack);
        if (uuid != null) {
            pTooltipComponents.add(Component.empty());
            pTooltipComponents.add(Component.literal("UUID: " + uuid).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(
                Component.translatable("tooltip.peacecraftgridmap.grid_map.scale", getScale(pStack) + 1)
                    .withStyle(ChatFormatting.GRAY)
            );
            pTooltipComponents.add(Component.empty());

        }
    }
}
