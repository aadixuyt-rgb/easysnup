package pl.easysnup.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.easysnup.Costume;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    /** Glowa Snupa + blekitny pancerz - widoczne tylko dla Ciebie. */
    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;"
            + "Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void easysnup$costumeLook(AbstractClientPlayerEntity player, PlayerEntityRenderState state,
                                      float tickDelta, CallbackInfo ci) {
        if (Costume.worn && player == MinecraftClient.getInstance().player) {
            Costume.applyLook(state, player);
        }
    }
}
