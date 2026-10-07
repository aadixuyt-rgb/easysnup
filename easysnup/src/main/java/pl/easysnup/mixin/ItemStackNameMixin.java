package pl.easysnup.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.easysnup.Disguise;

@Mixin(ItemStack.class)
public abstract class ItemStackNameMixin {
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void easysnup$name(CallbackInfoReturnable<Text> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (Disguise.matches(self)) {
            cir.setReturnValue(Disguise.name(self));
        }
    }
}
