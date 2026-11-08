package de.nicknames.mixin;

import de.nicknames.Nicknames;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Nach dem Befüllen des Render-States ersetzen wir den Namensschild-Text
 * von Spielern, für die du einen Spitznamen gesetzt hast. Nur clientseitig.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void nicknames$apply(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (state.nameTag == null || !(entity instanceof Player player)) return;
        String nick = Nicknames.get(player.getName().getString());
        if (nick != null) {
            state.nameTag = Component.literal(nick.replace('&', '§'));
        }
    }
}
