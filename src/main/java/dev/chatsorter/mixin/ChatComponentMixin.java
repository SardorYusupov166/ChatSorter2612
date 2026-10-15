package dev.chatsorter.mixin;

import dev.chatsorter.ChatSorter;
import dev.chatsorter.ChatPosition;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    // Every incoming player/system message goes through the common entry point.
    @Inject(method = "addMessage", at = @At("HEAD"))
    private void chatsorter$record(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        ChatSorter.capture(contents);
    }
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"))
    private void chatsorter$translate(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY,
            @Coerce Object displayMode, boolean changeCursor, CallbackInfo ci) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(ChatPosition.x, ChatPosition.y);
    }
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("RETURN"))
    private void chatsorter$restore(GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY,
            @Coerce Object displayMode, boolean changeCursor, CallbackInfo ci) {
        graphics.pose().popMatrix();
    }
}
