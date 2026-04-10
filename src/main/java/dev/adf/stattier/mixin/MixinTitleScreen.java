package dev.adf.stattier.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Component title) {
        super(title);
    }
}
