package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;

public final class Titles {
    private Titles() {}

    public static void show(String title, String subtitle) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud == null) return;
        mc.inGameHud.setTitleTicks(10, 40, 10);
        mc.inGameHud.setSubtitle(Legacy.parse(subtitle));
        mc.inGameHud.setTitle(Legacy.parse(title));
    }

    public static void sound(SoundEvent event, float volume, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSoundManager() == null) return;
        mc.getSoundManager().play(PositionedSoundInstance.master(event, pitch, volume));
    }

    public static void chat(String legacy) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(Legacy.parse(legacy));
    }
}
