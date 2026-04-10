package dev.adf.stattier.config;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.tierlist.PlayerSearchScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import net.uku3lig.ukulib.utils.Ukutils;

public class TTConfigScreen extends AbstractConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super(parent, Text.of("StatTier Config"), TierTagger.getManager());
    }

    @Override
    protected SimpleOption<?>[] getOptions(TierTaggerConfig config) {
        return new SimpleOption[]{
                Ukutils.createBooleanOption("stattier.config.enabled", config.isEnabled(), config::setEnabled),
                Ukutils.createCyclingOption("stattier.config.gamemode", TierCache.getGamemodes(), config.getGameMode(),
                        (GameMode m) -> config.setGameMode(m.id()),
                        (GameMode m) -> Text.literal(m.title()),
                        (GameMode m) -> m.isNone() ? Tooltip.of(Text.translatable("stattier.config.gamemode.none")) : null),
                Ukutils.createBooleanOption("stattier.config.retired", config.isShowRetired(), config::setShowRetired),
                Ukutils.createEnumOption("stattier.config.highest", TierTaggerConfig.HighestMode.class,
                        TierTaggerConfig.HighestMode::byId, config.getHighestMode(), config::setHighestMode,
                        SimpleOption.constantTooltip(Text.translatable("stattier.config.highest.desc"))),
                Ukutils.createBooleanOption("stattier.config.icons", config.isShowIcons(), config::setShowIcons),
                Ukutils.createBooleanOption("stattier.config.playerList", config.isPlayerList(), config::setPlayerList),
                Ukutils.createButton("stattier.clear", b -> TierCache.clearCache()),
                Ukutils.createOpenButton("stattier.config.search", PlayerSearchScreen::new)
        };
    }
}
