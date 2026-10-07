package pl.easysnup.mixin;

import net.minecraft.client.render.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.easysnup.Disguise;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    private static final String UPDATE =
            "update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;"
                    + "Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/world/World;"
                    + "Lnet/minecraft/entity/LivingEntity;I)V";

    /** Przebrany przedmiot rysujemy jako ikone Kostiumu Snupa. */
    @ModifyVariable(method = UPDATE, at = @At("HEAD"), argsOnly = true)
    private ItemStack easysnup$look(ItemStack stack) {
        return Disguise.model(stack);
    }

    /** Zalozony kostium jest niewidzialny. */
    @Inject(method = UPDATE, at = @At("HEAD"), cancellable = true)
    private void easysnup$hidden(ItemRenderState state, ItemStack stack, ModelTransformationMode mode,
                                 boolean leftHand, World world, LivingEntity entity, int seed, CallbackInfo ci) {
        if (Disguise.isHidden(stack)) {
            state.clear();
            ci.cancel();
        }
    }
}
