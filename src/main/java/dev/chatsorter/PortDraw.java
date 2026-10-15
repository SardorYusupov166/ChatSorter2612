package dev.chatsorter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
public final class PortDraw {
    private PortDraw() {}
    public static void drawTextWithShadow(GuiGraphicsExtractor graphics, Font font, String str, int x, int y, int color) {
        graphics.text(font, str, x, y, color, true);
    }
    public static void drawCenteredTextWithShadow(GuiGraphicsExtractor graphics, Font font, String str, int centerX, int y, int color) {
        graphics.text(font, str, centerX - font.width(str)/2, y, color, true);
    }
}
