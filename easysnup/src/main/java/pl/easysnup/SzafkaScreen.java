package pl.easysnup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.IntConsumer;

/** Ekran wygladajacy jak skrzynia (generic_54) - lokalna "szafka". */
public class SzafkaScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");

    public record Entry(int slot, ItemStack icon, List<Text> tooltip, IntConsumer action) {}

    private final int rows;
    private final int bgHeight;
    private final List<Entry> entries;
    private int left;
    private int top;

    public SzafkaScreen(Text title, int rows, List<Entry> entries) {
        super(title);
        this.rows = rows;
        this.entries = entries;
        this.bgHeight = 114 + rows * 18;
    }

    @Override
    protected void init() {
        left = (width - 176) / 2;
        top = (height - bgHeight) / 2;
    }

    private int slotX(int slot) {
        return left + 8 + (slot % 9) * 18;
    }

    private int slotY(int slot) {
        return top + 18 + (slot / 9) * 18;
    }

    private int mainTop() {
        return top + 103 + (rows - 4) * 18;
    }

    private int hotbarTop() {
        return top + 161 + (rows - 4) * 18;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawTexture(RenderLayer::getGuiTextured, TEXTURE, left, top, 0f, 0f, 176, rows * 18 + 17, 256, 256);
        ctx.drawTexture(RenderLayer::getGuiTextured, TEXTURE, left, top + rows * 18 + 17, 0f, 126f, 176, 96, 256, 256);
        ctx.drawText(textRenderer, title, left + 8, top + 6, 0x404040, false);
        ctx.drawText(textRenderer, Text.translatable("container.inventory"), left + 8, top + bgHeight - 94, 0x404040, false);

        drawPlayerInventory(ctx);

        Entry hovered = null;
        for (Entry e : entries) {
            int x = slotX(e.slot());
            int y = slotY(e.slot());
            ctx.drawItem(e.icon(), x, y);
            if (isOver(x, y, mouseX, mouseY)) {
                hovered = e;
                ctx.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
            }
        }
        if (hovered != null && !hovered.tooltip().isEmpty()) {
            ctx.drawTooltip(textRenderer, hovered.tooltip(), mouseX, mouseY);
        }
    }

    private void drawPlayerInventory(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        PlayerInventory inv = mc.player.getInventory();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                ItemStack st = inv.getStack(9 + row * 9 + col);
                int x = left + 8 + col * 18;
                int y = mainTop() + row * 18;
                ctx.drawItem(st, x, y);
                ctx.drawStackOverlay(textRenderer, st, x, y, null);
            }
        }
        for (int col = 0; col < 9; col++) {
            ItemStack st = inv.getStack(col);
            int x = left + 8 + col * 18;
            int y = hotbarTop();
            ctx.drawItem(st, x, y);
            ctx.drawStackOverlay(textRenderer, st, x, y, null);
        }
    }

    private static boolean isOver(int x, int y, double mx, double my) {
        return mx >= x && mx < x + 16 && my >= y && my < y + 16;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Entry e : entries) {
            if (isOver(slotX(e.slot()), slotY(e.slot()), mouseX, mouseY)) {
                e.action().accept(button);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
