package icu.takeneko.gridmap.item;

import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.networking.ServerboundRequestOpenMapPacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GridMapBookItem extends Item {
    public GridMapBookItem(Properties pProperties) {
        super(pProperties);
    }

    public static List<UUID> getGridMapsInItem(ItemStack itemStack) {
        List<UUID> resultList = new ArrayList<>();
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("Maps") && tag.get("Maps") instanceof ListTag listTag) {
            for (Tag tag1 : listTag) {
                resultList.add(NbtUtils.loadUUID(tag1));
            }
        }
        return resultList;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            List<UUID> maps = getGridMapsInItem(itemStack);
            if (!maps.isEmpty()) {
                AllNetworking.sendToServer(new ServerboundRequestOpenMapPacket(maps));
            }
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(
        ItemStack pStack,
        @Nullable Level pLevel,
        List<Component> pTooltipComponents,
        TooltipFlag pIsAdvanced
    ) {
        List<UUID> list = getGridMapsInItem(pStack);
        pTooltipComponents.add(Component.translatable("tooltip.peacecraftgridmap.grid_map_book"));
        for (UUID uuid : list) {
            pTooltipComponents.add(Component.literal("  " + uuid));
        }
    }

    public static ItemStack updateGridMapsInItem(ItemStack itemStack, List<UUID> maps) {
        CompoundTag tag = itemStack.getOrCreateTag();
        ListTag listTag = new ListTag();
        for (UUID uuid : maps) {
            System.out.println("uuid = " + uuid);
            if (uuid == null) continue;
            listTag.add(NbtUtils.createUUID(uuid));
        }
        tag.put("Maps", listTag);
        return itemStack.copy();
    }
}
