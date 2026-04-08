package dev.adf.stattier.mixin;

import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({class_442.class})
public class MixinTitleScreen extends class_437 {
   protected MixinTitleScreen(class_2561 title) {
      super(title);
   }
}
