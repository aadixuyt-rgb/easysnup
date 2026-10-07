package pl.easysnup;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class EasySnupClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.easysnup.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.easysnup"));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerSzafka(dispatcher, "szafka");
            registerSzafka(dispatcher, "szafa");
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openMenuKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new SnupScreen());
            }
            Costume.tick(mc);
        });

        // Prawy klik przedmiotem-kostiumem: zalozenie (bez wysylania do serwera).
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient || hand != Hand.MAIN_HAND) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            return Costume.onRightClick(stack) ? ActionResult.FAIL : ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!world.isClient || hand != Hand.MAIN_HAND) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);
            return Costume.onRightClick(stack) ? ActionResult.FAIL : ActionResult.PASS;
        });
    }

    private static void registerSzafka(CommandDispatcher<FabricClientCommandSource> dispatcher, String name) {
        dispatcher.register(ClientCommandManager.literal(name).executes(ctx -> {
            // send() - otwarcie dopiero w nastepnym ticku, zeby czat nie zamknal ekranu.
            MinecraftClient.getInstance().send(Szafka::open);
            return 1;
        }));
    }
}
