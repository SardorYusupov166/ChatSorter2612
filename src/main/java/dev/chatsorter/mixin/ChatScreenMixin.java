package dev.chatsorter.mixin;

import dev.chatsorter.ChatSorter;
import dev.chatsorter.ChatPosition;
import dev.chatsorter.KeywordAlerts;
import dev.chatsorter.PortDraw;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    private EditBox chatsorter$search;
    @Inject(method = "init", at = @At("TAIL"))
    private void chatsorter$initSearch(CallbackInfo ci) {
        int w = Math.min(210, Math.max(100, this.width / 3));
        chatsorter$search = new EditBox(this.font, this.width - w - 5, 29, w, 17, Component.literal("Поиск во всех чатах"));
        chatsorter$search.setMaxLength(120);
        chatsorter$search.setHint(Component.literal("Поиск во всех чатах..."));
        chatsorter$search.setValue(ChatSorter.searchQuery());
        chatsorter$search.setResponder(ChatSorter::setSearchQuery);
        this.addRenderableWidget(chatsorter$search);
    }
    private boolean chatsorter$dragging = false;
    private boolean chatsorter$resizing = false;
    private boolean chatsorter$resizeLeft, chatsorter$resizeRight;
    private boolean chatsorter$resizeTop, chatsorter$resizeBottom;
    private double chatsorter$lastX, chatsorter$lastY;
    protected ChatScreenMixin(Component title) { super(title); }

    // Draw flat tabs above the chat instead of adding vanilla ButtonWidgets.
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void chatsorter$render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        chatsorter$pollDrag();
        // Invisible hit areas: drag the message body; resize by grabbing its edges.
        ChatSorter.Tab[] tabs = ChatSorter.Tab.values();
        int x = chatsorter$tabX();
        int y = 7;
        int width = Math.max(54, Math.min(100, (this.width - 12) / tabs.length));
        int h = 17;
        for (int i = 0; i < tabs.length; i++) {
            int left = x + i * width;
            boolean active = ChatSorter.selected == tabs[i];
            boolean hovered = mouseX >= left && mouseX < left + width - 2 && mouseY >= y && mouseY < y + h;
            int bg = active ? dev.chatsorter.ChatTheme.accent() : hovered ? 0xDB393E42 : 0xCB25282D;
            context.fill(left, y, left + width - 2, y + h, bg);
            if (active) context.fill(left, y + h - 2, left + width - 2, y + h, dev.chatsorter.ChatTheme.bright());
            String label = tabs[i].title;
            int textX = left + (width - 2 - this.font.width(label)) / 2;
            PortDraw.drawTextWithShadow(context,this.font, label, textX, y + 4, active ? dev.chatsorter.ChatTheme.text() : 0xFFD3D5D6);
            int count = ChatSorter.unread[tabs[i].ordinal()];
            if (count > 0) {
                String number = count > 999 ? "999+" : Integer.toString(count);
                int bx = left + width - 4 - this.font.width(number);
                context.fill(bx - 3, y - 7, left + width - 1, y + 3, 0xE0A53737);
                PortDraw.drawTextWithShadow(context,this.font, number, bx, y - 6, 0xFFFFFFFF);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatsorter$click(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) {
            double left = chatsorter$leftEdge();
            double right = chatsorter$rightEdge();
            double top = chatsorter$topEdge();
            double bottom = chatsorter$bottomEdge();
            double edge = 7;
            boolean withinX = mouseX >= left - edge && mouseX <= right + edge;
            boolean withinY = mouseY >= top - edge && mouseY <= bottom + edge;
            if (withinX && withinY && !(chatsorter$search != null && mouseX >= chatsorter$search.getX() && mouseX < chatsorter$search.getX() + chatsorter$search.getWidth() && mouseY >= chatsorter$search.getY() && mouseY < chatsorter$search.getY() + chatsorter$search.getHeight())) {
                chatsorter$resizeLeft = Math.abs(mouseX - left) <= edge;
                chatsorter$resizeRight = Math.abs(mouseX - right) <= edge;
                chatsorter$resizeTop = Math.abs(mouseY - top) <= edge;
                chatsorter$resizeBottom = Math.abs(mouseY - bottom) <= edge;
                chatsorter$resizing = chatsorter$resizeLeft || chatsorter$resizeRight
                    || chatsorter$resizeTop || chatsorter$resizeBottom;
                chatsorter$dragging = !chatsorter$resizing;
                chatsorter$lastX = mouseX;
                chatsorter$lastY = mouseY;
                cir.setReturnValue(true);
                return;
            }
        }
        if (click.button() == 1 && chatsorter$copyAt(mouseX, mouseY)) {
            cir.setReturnValue(true);
            return;
        }
        int x = chatsorter$tabX();
        int y = 7;
        int width = Math.max(54, Math.min(100, (this.width - 12) / ChatSorter.Tab.values().length));
        if (mouseY < y || mouseY >= y + 17) return;
        for (int i = 0; i < ChatSorter.Tab.values().length; i++) {
            int left = x + i * width;
            if (mouseX >= left && mouseX < left + width - 2) {
                ChatSorter.setSearchQuery("");
                if (chatsorter$search != null) chatsorter$search.setValue("");
                ChatSorter.switchTo(ChatSorter.Tab.values()[i]);
                cir.setReturnValue(true);
                return;
            }
        }
    }

    private int chatsorter$tabX() {
        int width = Math.max(54, Math.min(100, (this.width - 12) / ChatSorter.Tab.values().length));
        return Math.max(2, this.width - width * ChatSorter.Tab.values().length - 4);
    }

    private boolean chatsorter$copyAt(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        ChatComponent chat = mc.gui.getChat();
        double scale = mc.options.chatScale().get();
        if (scale <= 0) return false;
        double chatX = (mouseX - ChatPosition.x) / scale;
        double chatY = (this.height - 40 + ChatPosition.y - mouseY) / scale;
        double lineHeight = 9.0 * (mc.options.chatLineSpacing().get() + 1.0);
        int index = (int) Math.floor(chatY / lineHeight);
        if (chatX < 0 || chatX > 400 || index < 0 || chatY < 0) return false;
        List<GuiMessage.Line> lines = ((ChatComponentAccess) chat).chatsorter$getTrimmedMessages();
        if (index >= lines.size()) return false;
        // Every wrapped row points to the unabridged source component.
        String fullMessage = lines.get(index).parent().content().getString();
        if (fullMessage.isBlank()) return false;
        mc.keyboardHandler.setClipboard(ChatSorter.copyRenderedLine(fullMessage));
        mc.gui.getChat().addClientSystemMessage(Component.literal("[ChatSorter] Сообщение скопировано"));
        return true;
    }
    // ChatScreen does not declare mouseDragged in Minecraft 1.21.11.
    // Polling while the chat screen is open avoids a missing-target Mixin injection.
    private void chatsorter$pollDrag() {
        if (!chatsorter$dragging && !chatsorter$resizing) return;
        Minecraft mc = Minecraft.getInstance();
        if (GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
            if (chatsorter$resizing) mc.options.save();
            chatsorter$dragging = false;
            chatsorter$resizing = false;
            ChatPosition.save();
            return;
        }
        double x = mc.mouseHandler.xpos() * this.width / mc.getWindow().getWidth();
        double y = mc.mouseHandler.ypos() * this.height / mc.getWindow().getHeight();
        if (chatsorter$resizing) {
            double scale = mc.options.chatScale().get();
            if (scale > 0) {
                double dx = x - chatsorter$lastX;
                double dy = y - chatsorter$lastY;
                double currentWidth = 40 + mc.options.chatWidth().get() * 280;
                double currentHeight = 20 + mc.options.chatHeightFocused().get() * 160;
                double dw = chatsorter$resizeRight ? dx : chatsorter$resizeLeft ? -dx : 0;
                double dh = chatsorter$resizeTop ? -dy : chatsorter$resizeBottom ? dy : 0;
                double newWidth = Math.max(40, Math.min(320, currentWidth + dw / scale));
                double newHeight = Math.max(20, Math.min(180, currentHeight + dh / scale));
                double actualDw = (newWidth - currentWidth) * scale;
                double actualDh = (newHeight - currentHeight) * scale;
                mc.options.chatWidth().set(Math.max(0, Math.min(1, (newWidth - 40) / 280)));
                mc.options.chatHeightFocused().set(Math.max(0, Math.min(1, (newHeight - 20) / 160)));
                // The left and bottom edges stay under the pointer when dragged.
                if (chatsorter$resizeLeft) ChatPosition.move((int)Math.round(-actualDw), 0, this.width, this.height);
                if (chatsorter$resizeBottom) ChatPosition.move(0, (int)Math.round(actualDh), this.width, this.height);
            }
        } else {
            ChatPosition.move((int)Math.round(x - chatsorter$lastX),
                (int)Math.round(y - chatsorter$lastY), this.width, this.height);
        }
        chatsorter$lastX = x;
        chatsorter$lastY = y;
    }

    private int chatsorter$rightEdge() {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.options.chatScale().get();
        return (int) Math.round(ChatPosition.x + (40 + mc.options.chatWidth().get() * 280) * scale);
    }
    private int chatsorter$topEdge() {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.options.chatScale().get();
        return (int) Math.round(this.height - 40 + ChatPosition.y
            - (20 + mc.options.chatHeightFocused().get() * 160) * scale);
    }
    private int chatsorter$leftEdge() { return ChatPosition.x; }
    private int chatsorter$bottomEdge() { return this.height - 40 + ChatPosition.y; }

}