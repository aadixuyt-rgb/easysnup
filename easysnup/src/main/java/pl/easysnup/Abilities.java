package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundEvents;

import java.util.Locale;

/** Umiejetnosc kostiumu Snupa: 3x shift = pomniejszenie do 0,65 na 10 sekund. */
public final class Abilities {
    private static final int SHIFTS = 3;
    private static final long TIMEOUT_MS = 3000;
    private static final long DURATION_MS = 10_000;
    private static final long COOLDOWN_MS = 120_000;

    /** Blekitne czasteczki (zamiast fioletowych jak u mima). */
    private static final DustParticleEffect BLUE_DUST = new DustParticleEffect(0x55AAFF, 1.0f);

    private static final Bar PROGRESS = new Bar("ability_progress");
    private static final Bar ACTIVE = new Bar("ability_active");
    private static final Bar COOLDOWN = new Bar("ability_cooldown");

    private static int shifts = 0;
    private static boolean sneakHeld = false;
    private static long lastShift = 0;
    private static boolean active = false;
    private static long activeUntil = 0;
    private static long readyAt = 0;
    private static double previousScale = 1.0;
    private static int particleTicks = 0;

    private Abilities() {}

    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null || !Costume.worn) {
            if (active || shifts > 0 || sneakHeld) reset();
            COOLDOWN.hide();
            return;
        }
        long now = System.currentTimeMillis();

        if (active) {
            if (now >= activeUntil) {
                end(p, now);
            } else {
                applyScale(p, Snup.SCALE);
                long left = activeUntil - now;
                ACTIVE.show("&7Kostium " + Snup.NAME + "&7 jest aktywny jeszcze przez &f" + seconds(left) + "&7!",
                        (float) left / DURATION_MS, BossBar.Color.BLUE);
                if (++particleTicks % 2 == 0) ring(p);
                return;
            }
        }

        if (now < readyAt) {
            shifts = 0;
            PROGRESS.hide();
            long left = readyAt - now;
            COOLDOWN.show("&7Kostium " + Snup.NAME + "&7 wykorzystany! Odnawianie umiejętności... &8("
                    + (long) Math.ceil(left / 1000.0) + "s)", (float) left / COOLDOWN_MS, BossBar.Color.RED);
            sneakHeld = mc.options.sneakKey.isPressed();
            return;
        }
        COOLDOWN.hide();
        countShifts(mc, p, now);
    }

    private static void countShifts(MinecraftClient mc, ClientPlayerEntity p, long now) {
        boolean down = mc.options.sneakKey.isPressed();
        if (down && !sneakHeld) {
            if (shifts > 0 && now - lastShift > TIMEOUT_MS) shifts = 0;
            shifts++;
            lastShift = now;
            if (shifts >= SHIFTS) {
                activate(p, now);
                sneakHeld = down;
                return;
            }
        }
        sneakHeld = down;

        if (shifts > 0) {
            long left = TIMEOUT_MS - (now - lastShift);
            if (left <= 0) {
                shifts = 0;
                PROGRESS.hide();
            } else {
                int missing = SHIFTS - shifts;
                PROGRESS.show("&5Shiftnij jeszcze &f" + missing + " &5" + (missing == 1 ? "raz" : "razy")
                                + ", aby uruchomić &dpomniejszenie&5! &7(" + seconds(left) + ")",
                        (float) left / TIMEOUT_MS, BossBar.Color.BLUE);
            }
        } else {
            PROGRESS.hide();
        }
    }

    private static void activate(ClientPlayerEntity p, long now) {
        shifts = 0;
        PROGRESS.hide();
        active = true;
        activeUntil = now + DURATION_MS;
        EntityAttributeInstance inst = p.getAttributeInstance(EntityAttributes.SCALE);
        if (inst != null) previousScale = inst.getBaseValue();
        applyScale(p, Snup.SCALE);
        Titles.show("&b&lKOSTIUM SNUPA", "&7Aktywowano &dpomniejszenie&7!");
        Titles.sound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0f, 1.6f);
        burst(p);
    }

    private static void end(ClientPlayerEntity p, long now) {
        active = false;
        ACTIVE.hide();
        applyScale(p, previousScale);
        readyAt = now + COOLDOWN_MS;
        Titles.show("&b&lKOSTIUM SNUPA", "&fPomniejszenie się skończyło!");
        Titles.sound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.2f);
    }

    /** Przywraca normalny rozmiar i chowa paski (np. po zdjeciu kostiumu). */
    public static void reset() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (active && mc.player != null) {
            applyScale(mc.player, previousScale);
        }
        active = false;
        shifts = 0;
        sneakHeld = false;
        PROGRESS.hide();
        ACTIVE.hide();
        COOLDOWN.hide();
    }

    private static void applyScale(ClientPlayerEntity p, double value) {
        EntityAttributeInstance inst = p.getAttributeInstance(EntityAttributes.SCALE);
        if (inst != null && inst.getBaseValue() != value) {
            inst.setBaseValue(value);
            p.calculateDimensions();
        }
    }

    private static String seconds(long ms) {
        return String.format(Locale.ROOT, "%.1fs", ms / 1000.0);
    }

    private static void ring(ClientPlayerEntity p) {
        double r = 0.7 * p.getScale();
        double t = particleTicks * 0.3;
        for (int i = 0; i < 4; i++) {
            double a = t + i * Math.PI / 2;
            p.getWorld().addParticle(BLUE_DUST, p.getX() + Math.cos(a) * r,
                    p.getY() + p.getHeight() * 0.5, p.getZ() + Math.sin(a) * r, 0, 0, 0);
        }
    }

    private static void burst(ClientPlayerEntity p) {
        double r = 0.9;
        for (int i = 0; i < 24; i++) {
            double a = i * (Math.PI * 2) / 12.0;
            double h = (i / 12) * 0.9 + 0.2;
            p.getWorld().addParticle(BLUE_DUST, p.getX() + Math.cos(a) * r,
                    p.getY() + h, p.getZ() + Math.sin(a) * r, 0, 0.02, 0);
        }
    }
}
