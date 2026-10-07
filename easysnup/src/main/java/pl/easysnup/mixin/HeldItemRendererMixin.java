package pl.easysnup.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pl.easysnup.Disguise;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    /**
     * Niewidzialny (zalozony) kostium traktujemy jak pusta reke, dzieki czemu reka
     * nie "odstaje" tak, jakbys cos trzymal.
     */
    @ModifyVariable(method = "renderFirstPersonItem", at = @At("HEAD"), argsOnly = true)
    private ItemStack easysnup$hiddenAsEmpty(ItemStack stack) {
        return Disguise.isHidden(stack) ? ItemStack.EMPTY : stack;
    }
}
