package icu.takeneko.gridmap.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$5")
public class CartographyTableMenuResultSlotMixin {

//    @Shadow(remap = false)
//    @Final
//    CartographyTableMenu this$0;

    @Inject(method = "onTake", at = @At("HEAD"))
    void runOnTake(Player p_150509_, ItemStack p_150510_, CallbackInfo ci) {
        AtomicReference<Runnable> reference = ((Supplier<AtomicReference<Runnable>>) (p_150509_.containerMenu)).get();
        if (reference.get() != null) {
            reference.get().run();
            reference.set(null);
        }
    }
}
