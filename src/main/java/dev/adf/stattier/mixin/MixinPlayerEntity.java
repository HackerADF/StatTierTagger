package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    public Text prependTier(Text original) {
        if (TierTagger.getManager().getConfig().isEnabled()) {
            PlayerEntity self = (PlayerEntity) (Object) this;
            return TierTagger.appendTier(self.getNameForScoreboard(), original);
        } else {
            return original;
        }
    }
}
