package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class MixinPlayerEntity {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    public Component prependTier(Component original) {
        if (TierTagger.getManager().getConfig().isEnabled()) {
            Player self = (Player) (Object) this;
            return TierTagger.appendTier(self.getScoreboardName(), original);
        } else {
            return original;
        }
    }
}
