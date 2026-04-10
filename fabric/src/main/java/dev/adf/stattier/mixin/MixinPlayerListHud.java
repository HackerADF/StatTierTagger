package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import dev.adf.stattier.config.TierTaggerConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerListHud {
    @ModifyReturnValue(method = "getNameForDisplay", at = @At("RETURN"))
    @Nullable
    public Component prependTier(Component original, PlayerInfo entry) {
        TierTaggerConfig config = TierTagger.getManager().getConfig();
        if (config.isEnabled() && config.isPlayerList()) {
            return TierTagger.appendTier(entry.getProfile().getName(), original);
        } else {
            return original;
        }
    }
}
