package dev.adf.stattier.mixin;

import dev.adf.stattier.TierTagger;
import dev.adf.stattier.config.TierTaggerConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.class_2561;
import net.minecraft.class_355;
import net.minecraft.class_640;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_355.class})
public class MixinPlayerListHud {
   @ModifyReturnValue(
      method = {"method_1918"},
      at = {@At("RETURN")}
   )
   @Nullable
   public class_2561 prependTier(class_2561 original, class_640 entry) {
      TierTaggerConfig config = (TierTaggerConfig)TierTagger.getManager().getConfig();
      return config.isEnabled() && config.isPlayerList() ? TierTagger.appendTier(entry.method_2966().getName(), original) : original;
   }
}
