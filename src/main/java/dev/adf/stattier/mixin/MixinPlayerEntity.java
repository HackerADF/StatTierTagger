package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import dev.adf.stattier.config.TierTaggerConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1657.class})
public class MixinPlayerEntity {
   @ModifyReturnValue(
      method = {"method_5476"},
      at = {@At("RETURN")}
   )
   public class_2561 prependTier(class_2561 original) {
      if (((TierTaggerConfig)TierTagger.getManager().getConfig()).isEnabled()) {
         class_1657 self = (class_1657)(Object)this;
         return TierTagger.appendTier(self.method_5820(), original);
      } else {
         return original;
      }
   }
}
