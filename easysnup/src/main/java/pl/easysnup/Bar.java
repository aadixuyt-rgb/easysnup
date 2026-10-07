package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import pl.easysnup.mixin.BossBarHudAccessor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/** Lokalny (nieprawdziwy) boss bar. */
public final class Bar {
    private final UUID id;

    public Bar(String name) {
        this.id = UUID.nameUUIDFromBytes(("easysnup:" + name).getBytes(StandardCharsets.UTF_8));
    }

    private Map<UUID, ClientBossBar> bars() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return ((BossBarHudAccessor) mc.inGameHud.getBossBarHud()).easysnup$getBossBars();
    }

    public void show(String legacyText, float percent, BossBar.Color color) {
        Map<UUID, ClientBossBar> bars = bars();
        ClientBossBar bar = bars.get(id);
        if (bar == null) {
            bar = new ClientBossBar(id, Legacy.parse(legacyText), percent, color,
                    BossBar.Style.PROGRESS, false, false, false);
            bars.put(id, bar);
        }
        bar.setName(Legacy.parse(legacyText));
        bar.setColor(color);
        bar.setPercent(Math.max(0f, Math.min(1f, percent)));
    }

    public void hide() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            bars().remove(id);
        }
    }
}
