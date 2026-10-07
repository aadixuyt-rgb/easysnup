package pl.easysnup;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Dane kostiumu Snupa: nazwy, kolory, lore. */
public final class Snup {
    /** Nazwa w dopelniaczu - "snupa" na blekitno. */
    public static final String NAME = "&bsnupa";
    public static final String TITLE = "&7Kostium " + NAME;
    /** Blekitny barwnik (light blue). */
    public static final int DYE = 0x3AB3DA;

    public static final double SCALE = 0.65;
    public static final float BONUS_HEALTH = 4.0f; // +2 serca = 4 punkty zdrowia

    private static final List<String> LORE = List.of(
            "&7Został on dodany przez &csnupxb&7.",
            "",
            "&7Dzięki temu przedmiotowi otrzymasz",
            "&funikatowy &7wygląd oraz &fepickie &7bonusy",
            "",
            "&7Lista bonusów:",
            " &8» &c5% &7do każdego perka",
            " &8» &4+2 &7serc",
            " &8» &cPomniejszenie&7, które pomniejsza",
            "&7Cię do rozmiaru &e0,65 &8(3x shift)"
    );

    private Snup() {}

    public static Text name() {
        return Legacy.parse(TITLE);
    }

    /** Pelny tooltip: nazwa, bonusy, ewentualna data i podpowiedz. */
    public static List<Text> tooltip(boolean worn) {
        List<Text> out = new ArrayList<>();
        out.add(name());
        for (String l : LORE) out.add(Legacy.parse(l));
        out.add(Legacy.parse(""));
        if (!Config.forever) {
            out.add(Legacy.parse("&7Kostium wygaśnie: &f" + Config.expiryText()));
        }
        out.add(Legacy.parse(worn ? "&cKliknij, aby zdjąć kostium!" : "&aKliknij prawym, aby założyć!"));
        return out;
    }
}
