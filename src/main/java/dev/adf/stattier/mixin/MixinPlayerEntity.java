package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    public void prependTier(CallbackInfoReturnable<Text> cir) {
        if (TierTagger.getManager().getConfig().isEnabled()) {
            PlayerEntity self = (PlayerEntity) (Object) this;
            Text modified = TierTagger.appendTier(self.getEntityName(), cir.getReturnValue());
            cir.setReturnValue(modified);
        }
    }
}
