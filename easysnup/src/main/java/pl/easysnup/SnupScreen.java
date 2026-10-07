package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.time.LocalDateTime;

/** Menu moda (klawisz B). */
public class SnupScreen extends Screen {
    private TextFieldWidget dateField;
    private ButtonWidget mainButton;
    private ButtonWidget foreverButton;
    private String status = "";
    private int statusColor = 0xAAAAAA;

    public SnupScreen() {
        super(Text.literal("EasySnup"));
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 55;

        mainButton = addDrawableChild(ButtonWidget.builder(mainLabel(), b -> onMain())
                .dimensions(cx - 100, y + 25, 200, 20).build());

        dateField = new TextFieldWidget(textRenderer, cx - 100, y + 75, 200, 20, Text.literal("Data"));
        dateField.setMaxLength(32);
        dateField.setText(Config.expiryText());
        dateField.setEditable(!Config.forever);
        dateField.setChangedListener(this::onDateEdited);
        addDrawableChild(dateField);

        foreverButton = addDrawableChild(ButtonWidget.builder(foreverLabel(), b -> {
            Config.forever = !Config.forever;
            Config.save();
            foreverButton.setMessage(foreverLabel());
            dateField.setEditable(!Config.forever);
        }).dimensions(cx - 100, y + 100, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), b -> close())
                .dimensions(cx - 50, y + 135, 100, 20).build());
    }

    private Text mainLabel() {
        if (Variant.give()) {
            return Text.literal("GIVE - daj sobie Kostium Snupa");
        }
        boolean on = !Config.fingerprint.isEmpty();
        return Text.literal("Kostium Snupa: " + (on ? "WŁĄCZONY (kliknij, aby wyłączyć)" : "WYŁĄCZONY"));
    }

    private Text foreverLabel() {
        return Text.literal("Data: na zawsze (nie pokazuj daty): " + (Config.forever ? "TAK" : "NIE"));
    }

    private void onMain() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            error("Musisz być w grze.");
            return;
        }
        if (Variant.give()) {
            if (Costume.give()) ok("Gotowe — przedmiot widzisz tylko Ty.");
            else error("Nie udało się dać kostiumu (sprawdź czat).");
            return;
        }
        ItemStack held = mc.player.getMainHandStack();
        if (!Config.fingerprint.isEmpty() && Config.fingerprint.equals(Disguise.fingerprint(held))) {
            Disguise.setFingerprint("");
            ok("Wyłączone — przedmiot wygląda znowu normalnie.");
        } else if (held.isEmpty()) {
            error("Weź coś do ręki.");
            return;
        } else {
            Disguise.setFingerprint(Disguise.fingerprint(held));
            ok("Gotowe — widzisz to tylko Ty. Kliknij go prawym, aby założyć.");
        }
        mainButton.setMessage(mainLabel());
    }

    private void onDateEdited(String text) {
        try {
            LocalDateTime t = LocalDateTime.parse(text.trim(), Config.DATE);
            Config.setExpiry(t);
            dateField.setEditableColor(0xE0E0E0);
        } catch (Exception e) {
            dateField.setEditableColor(0xFF5555);
        }
    }

    private void ok(String s) {
        status = s;
        statusColor = 0x55FF55;
    }

    private void error(String s) {
        status = s;
        statusColor = 0xFF5555;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int y = height / 2 - 55;
        ctx.drawCenteredTextWithShadow(textRenderer, Legacy.parse("&b&lEasySnup"), cx, y - 5, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Legacy.parse(Snup.TITLE), cx, y + 12, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Data ważności (dd-MM-yyyy, HH:mm:ss)"), cx, y + 63, 0xAAAAAA);
        if (!status.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(status), cx, y + 165, statusColor);
        }
    }
}
