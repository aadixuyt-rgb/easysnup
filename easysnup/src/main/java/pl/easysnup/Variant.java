package pl.easysnup;

import net.fabricmc.loader.api.FabricLoader;

public final class Variant {
    private static final boolean GIVE = detect();

    private Variant() {}

    private static boolean detect() {
        return FabricLoader.getInstance().getModContainer("easysnup")
                .map(c -> c.getMetadata().getCustomValue("easysnup:variant"))
                .map(v -> "give".equals(v.getAsString()))
                .orElse(false);
    }

    /** true = wersja z przyciskiem GIVE, false = wersja z podmianą trzymanego przedmiotu. */
    public static boolean give() {
        return GIVE;
    }
}
