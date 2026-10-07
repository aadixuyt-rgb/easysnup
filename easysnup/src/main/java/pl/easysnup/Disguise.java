package pl.easysnup;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Rozpoznawanie przedmiotu-kostiumu i jego wyglad (tylko po stronie klienta). */
public final class Disguise {
    public static final String GHOST_KEY = "easysnup_costume";

    private static Item fpItem = null;
    private static ItemStack look;

    private Disguise() {}

    // ---------- odcisk przedmiotu (wersja swap) ----------

    public static String fingerprint(ItemStack s) {
        List<String> parts = new ArrayList<>();
        ComponentChanges changes = s.getComponentChanges();
        for (Map.Entry<?, ? extends Optional<?>> e : changes.entrySet()) {
            parts.add(e.getKey() + "=" + String.valueOf(e.getValue().orElse(null)));
        }
        Collections.sort(parts);
        return Registries.ITEM.getId(s.getItem()) + "|" + String.join(";", parts);
    }

    public static void setFingerprint(String fp) {
        Config.fingerprint = fp;
        Config.save();
        refreshFingerprint();
    }

    public static void refreshFingerprint() {
        String fp = Config.fingerprint;
        if (fp == null || fp.isEmpty()) {
            fpItem = null;
            return;
        }
        int i = fp.indexOf('|');
        Identifier id = Identifier.tryParse(i < 0 ? fp : fp.substring(0, i));
        fpItem = id == null ? null : Registries.ITEM.get(id);
    }

    // ---------- rozpoznawanie ----------

    public static boolean matches(ItemStack s) {
        if (s == null || s.isEmpty()) return false;
        if (Variant.give()) {
            NbtComponent c = s.get(DataComponentTypes.CUSTOM_DATA);
            return c != null && c.contains(GHOST_KEY);
        }
        if (fpItem == null || s.getItem() != fpItem) return false;
        return Config.fingerprint.equals(fingerprint(s));
    }

    /** Przedmiot jest kostiumem, ktory juz masz na sobie - ma byc niewidzialny. */
    public static boolean isHidden(ItemStack s) {
        return Costume.worn && matches(s);
    }

    public static boolean isShown(ItemStack s) {
        return !Costume.worn && matches(s);
    }

    // ---------- wyglad ----------

    /** Ikona kostiumu: barwiona na blekitno skorzana ochrona dla konia. */
    public static ItemStack look() {
        if (look == null) {
            ItemStack st = new ItemStack(Items.LEATHER_HORSE_ARMOR);
            st.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Snup.DYE, false));
            look = st;
        }
        return look;
    }

    /** Stack, ktory faktycznie jest rysowany zamiast prawdziwego. */
    public static ItemStack model(ItemStack s) {
        return isShown(s) ? look() : s;
    }

    /** Przedmiot dawany przyciskiem GIVE (istnieje tylko u Ciebie). */
    public static ItemStack ghost() {
        ItemStack st = look().copy();
        NbtCompound tag = new NbtCompound();
        tag.putBoolean(GHOST_KEY, true);
        st.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return st;
    }

    public static Text name(ItemStack s) {
        return isHidden(s) ? Text.empty() : Snup.name();
    }

    public static List<Text> tooltip(ItemStack s) {
        if (isHidden(s)) return new ArrayList<>();
        return new ArrayList<>(Snup.tooltip(false));
    }
}
