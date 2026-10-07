package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** /szafka i /szafa - lokalne menu kostiumow (tylko Kostium Snupa). */
public final class Szafka {
    private static final int SLOT_COSTUMES = 18; // lewy dolny rog
    private static final int SLOT_WORN = 13;     // srodek
    private static final int SLOT_COSTUME = 13;
    private static final int SLOT_BACK = 22;

    private Szafka() {}

    public static void open() {
        MinecraftClient.getInstance().setScreen(main());
    }

    private static List<Text> lines(String... legacy) {
        List<Text> out = new ArrayList<>();
        for (String l : legacy) out.add(Legacy.parse(l));
        return out;
    }

    public static SzafkaScreen main() {
        List<SzafkaScreen.Entry> entries = new ArrayList<>();

        entries.add(new SzafkaScreen.Entry(SLOT_COSTUMES, new ItemStack(Items.ARMOR_STAND),
                lines("&6Kostiumy",
                        " &8» &7W tym miejscu odnajdziesz pełną listę",
                        " &8» &7wszystkich dostępnych &ekostiumów",
                        " &8» &7wraz z ich efektami dodatkowymi!",
                        "",
                        " &8» &aKliknij &2LEWYM&a, aby przejść!"),
                b -> {
                    if (b == 0) MinecraftClient.getInstance().setScreen(costumes());
                }));

        if (Costume.worn) {
            entries.add(new SzafkaScreen.Entry(SLOT_WORN, Disguise.look(), costumeTooltip(true), b -> {
                Costume.takeOff();
                MinecraftClient.getInstance().setScreen(main());
            }));
        }

        return new SzafkaScreen(Legacy.parse("&8Szafka"), 3, entries);
    }

    public static SzafkaScreen costumes() {
        List<SzafkaScreen.Entry> entries = new ArrayList<>();

        entries.add(new SzafkaScreen.Entry(SLOT_COSTUME, Disguise.look(), costumeTooltip(Costume.worn), b -> {
            if (b == 1) {
                if (Costume.worn) {
                    Costume.takeOff();
                } else {
                    Costume.wear(false);
                }
                MinecraftClient.getInstance().setScreen(costumes());
            }
        }));

        entries.add(new SzafkaScreen.Entry(SLOT_BACK, new ItemStack(Items.ARROW), lines("&cPowrót"), b -> {
            MinecraftClient.getInstance().setScreen(main());
        }));

        return new SzafkaScreen(Legacy.parse("&8Lista kostiumów"), 3, entries);
    }

    private static List<Text> costumeTooltip(boolean worn) {
        return Snup.tooltip(worn);
    }
}
