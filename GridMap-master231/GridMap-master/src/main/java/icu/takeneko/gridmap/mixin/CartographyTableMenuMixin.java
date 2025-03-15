package icu.takeneko.gridmap.mixin;

import icu.takeneko.gridmap.item.GridMapItem;
import icu.takeneko.gridmap.map.MapData;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mixin(CartographyTableMenu.class)
public abstract class CartographyTableMenuMixin extends AbstractContainerMenu implements Supplier<AtomicReference<Runnable>> {
    @Unique
    private AtomicReference<Runnable> gridMap$actionOnTake = new AtomicReference<>();

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Shadow
    @Final
    private ResultContainer resultContainer;

    protected CartographyTableMenuMixin(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true)
    void handleGridMap(ItemStack pMap, ItemStack pFirstSlotStack, ItemStack pResultOutput, CallbackInfo ci) {
        final boolean[] shouldCancel = {false};
        this.access.execute((level, pos) -> {
            MapData mapData = MapData.getData(
                level.getServer().overworld().getDataStorage(),
                GridMapItem.getUUID(pMap)
            );
            if (mapData == null) return;
            ItemStack itemstack = pMap.copyWithCount(1);
            if (pFirstSlotStack.is(Items.PAPER) && !mapData.isLocked() && mapData.getScale() < 4) {
                int scale = Mth.clamp(mapData.getScale() + 1, 0, 4);
                itemstack = GridMapItem.putScale(itemstack, scale);
                gridMap$actionOnTake.set(() -> {
                    mapData.setScale(scale);
                    mapData.setDirty();
                });
                itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
                this.broadcastChanges();
            } else if (pFirstSlotStack.is(Items.GLASS_PANE) && !mapData.isLocked()) {
                itemstack = GridMapItem.markLocked(itemstack);
                gridMap$actionOnTake.set(() -> {
                    mapData.setLocked(true);
                    mapData.setDirty();
                });
                this.broadcastChanges();
            } else {
                if (!pFirstSlotStack.is(Items.MAP)) {
                    this.resultContainer.removeItemNoUpdate(2);
                    this.broadcastChanges();
                    return;
                }
                itemstack = pMap.copyWithCount(2);
                this.broadcastChanges();
            }
            if (!ItemStack.matches(itemstack, pResultOutput)) {
                this.resultContainer.setItem(2, itemstack);
                this.broadcastChanges();
            }
            shouldCancel[0] = true;
        });
        if (shouldCancel[0]) {
            ci.cancel();
        }
    }

    @Override
    public AtomicReference<Runnable> get() {
        return gridMap$actionOnTake;
    }
}
