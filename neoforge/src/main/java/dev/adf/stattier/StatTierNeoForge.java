package dev.adf.stattier;

import dev.adf.stattier.model.GameMode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;

@Mod(TierTagger.MOD_ID)
public class StatTierNeoForge {
    public StatTierNeoForge(IEventBus modBus) {
        TierTagger.init();

        modBus.addListener(this::registerKeyMappings);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyMapping keybinding = new KeyMapping("stattier.keybind.gamemode", -1, "stattier.name");
        event.register(keybinding);
    }
}
