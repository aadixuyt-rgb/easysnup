package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/** Stan kostiumu Snupa: zakladanie, zdejmowanie, wyglad pancerza. */
public final class Costume {
    /** true = kostium jest zalozony. */
    public static boolean worn = false;

    private static boolean wornViaItem = false;
    private static boolean ghostPending = false;
    private static int ghostSlot = 0;
    private static int ticks = 0;

    private static ItemStack head, chest, legs, feet;

    private Costume() {}

    // ---------- zakladanie / zdejmowanie ----------

    public static void wear(boolean viaItem) {
        if (worn) return;
        worn = true;
        wornViaItem = viaItem;
        if (Variant.give()) {
            clearGhosts();
            ghostPending = false;
        }
        Titles.sound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        Titles.chat("&7Założyłeś " + Snup.TITLE + "&7.");
    }

    public static void takeOff() {
        if (!worn) return;
        forget();
        if (Variant.give()) {
            ghostPending = true;
            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (p != null) keepGhost(p);
        }
        Titles.sound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.4f);
        Titles.chat("&7Zdjąłeś " + Snup.TITLE + "&7.");
    }

    /** Cicho wylacza kostium (np. gdy przedmiot zniknal z ekwipunku). */
    public static void forget() {
        worn = false;
        wornViaItem = false;
        Abilities.reset();
    }

    public static float bonusHealth() {
        return worn ? Snup.BONUS_HEALTH : 0.0f;
    }

    /** Prawy klik przedmiotem. Zwraca true, gdy przedmiot jest kostiumem (blokuje normalne uzycie). */
    public static boolean onRightClick(ItemStack stack) {
        if (!Disguise.matches(stack)) return false;
        if (!worn) wear(true);
        return true;
    }

    // ---------- GIVE (wersja give) ----------

    public static boolean give() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) return false;
        if (worn) {
            Titles.chat("&cMasz już założony kostium. Zdejmij go w &f/szafka&c.");
            return false;
        }
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < 36; i++) {
            if (Disguise.matches(inv.getStack(i))) {
                Titles.chat("&cMasz już ten kostium w ekwipunku.");
                return false;
            }
        }
        if (!inv.insertStack(Disguise.ghost())) {
            Titles.chat("&cBrak miejsca w ekwipunku!");
            return false;
        }
        for (int i = 0; i < 36; i++) {
            if (Disguise.matches(inv.getStack(i))) {
                ghostSlot = i;
                break;
            }
        }
        ghostPending = true;
        Titles.chat("&7Otrzymałeś " + Snup.TITLE + "&7. &8(widzisz go tylko Ty)");
        return true;
    }

    private static void clearGhosts() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) return;
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (Disguise.matches(inv.getStack(i))) inv.setStack(i, ItemStack.EMPTY);
        }
    }

    /** Serwer potrafi nadpisac slot - przywracamy nasz przedmiot na miejsce. */
    private static void keepGhost(ClientPlayerEntity p) {
        if (!ghostPending || worn) return;
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < 36; i++) {
            if (Disguise.matches(inv.getStack(i))) {
                ghostSlot = i;
                return;
            }
        }
        if (inv.getStack(ghostSlot).isEmpty()) {
            inv.setStack(ghostSlot, Disguise.ghost());
        } else {
            int e = inv.getEmptySlot();
            if (e >= 0 && e < 36) {
                inv.setStack(e, Disguise.ghost());
                ghostSlot = e;
            }
        }
    }

    // ---------- tick ----------

    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) {
            if (worn) forget();
            ghostPending = false;
            return;
        }
        ticks++;
        if (Variant.give()) {
            keepGhost(p);
        } else if (worn && wornViaItem && ticks % 10 == 0) {
            boolean carried = false;
            PlayerInventory inv = p.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                if (Disguise.matches(inv.getStack(i))) {
                    carried = true;
                    break;
                }
            }
            if (!carried) forget();
        }
        Abilities.tick(mc);
    }

    // ---------- wyglad: glowa ze skina + blekitny skorzany pancerz ----------

    private static ItemStack dyed(net.minecraft.item.Item item) {
        ItemStack st = new ItemStack(item);
        st.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Snup.DYE, false));
        return st;
    }

    private static void ensureStacks() {
        if (head == null) {
            head = new ItemStack(Items.PAPER);
            head.set(DataComponentTypes.ITEM_MODEL, Identifier.of("easysnup", "snup_head"));
            chest = dyed(Items.LEATHER_CHESTPLATE);
            legs = dyed(Items.LEATHER_LEGGINGS);
            feet = dyed(Items.LEATHER_BOOTS);
        }
    }

    /** Wywolywane z PlayerEntityRendererMixin tylko dla lokalnego gracza. */
    public static void applyLook(PlayerEntityRenderState state, AbstractClientPlayerEntity player) {
        ensureStacks();
        state.equippedHeadStack = head;
        state.equippedChestStack = chest;
        state.equippedLegsStack = legs;
        state.equippedFeetStack = feet;
        state.wearingSkullType = null;
        state.wearingSkullProfile = null;
        state.headItemRenderState.clear();
        MinecraftClient.getInstance().getItemModelManager().updateForLivingEntity(
                state.headItemRenderState, head, ModelTransformationMode.HEAD, false, player);
    }
}
