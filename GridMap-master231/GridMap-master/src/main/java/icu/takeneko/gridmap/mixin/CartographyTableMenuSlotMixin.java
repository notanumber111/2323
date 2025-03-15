package icu.takeneko.gridmap.mixin;

import icu.takeneko.gridmap.all.AllItems;
import icu.takeneko.gridmap.item.GridMapItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$3")
public class CartographyTableMenuSlotMixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    void checkMayPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
        if (itemStack.is(AllItems.GRID_MAP.get()) && GridMapItem.getUUID(itemStack) != null){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
