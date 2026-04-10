package dev.adf.stattier.config;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.tierlist.PlayerSearchScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.config.option.*;
import net.uku3lig.ukulib.config.option.widget.ButtonTab;
import net.uku3lig.ukulib.config.screen.TabbedConfigScreen;

import java.util.*;
import java.util.stream.Collectors;

public class TTConfigScreen extends TabbedConfigScreen<TierTaggerConfig> {
    public TTConfigScreen(Screen parent) {
        super("StatTier Config", parent, TierTagger.getManager());
    }

    @Override
    protected Tab[] getTabs(TierTaggerConfig config) {
        return new Tab[]{new MainSettingsTab(), new ColorsTab()};
    }

    public class MainSettingsTab extends ButtonTab<TierTaggerConfig> {
        public MainSettingsTab() {
            super("stattier.config", TTConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
            return new WidgetCreator[]{
                    CyclingOption.ofBoolean("stattier.config.enabled", config.isEnabled(), config::setEnabled),
                    new CyclingOption<>("stattier.config.gamemode", TierCache.getGamemodes(), config.getGameMode(),
                            (GameMode m) -> config.setGameMode(m.id()),
                            (GameMode m) -> Component.literal(m.title()),
                            (GameMode m) -> m.isNone() ? Tooltip.create(Component.translatable("stattier.config.gamemode.none")) : null),
                    CyclingOption.ofBoolean("stattier.config.retired", config.isShowRetired(), config::setShowRetired),
                    CyclingOption.ofTranslatableEnum("stattier.config.highest", TierTaggerConfig.HighestMode.class, config.getHighestMode(), config::setHighestMode, OptionInstance.cachedConstantTooltip(Component.translatable("stattier.config.highest.desc"))),
                    CyclingOption.ofBoolean("stattier.config.icons", config.isShowIcons(), config::setShowIcons),
                    CyclingOption.ofBoolean("stattier.config.playerList", config.isPlayerList(), config::setPlayerList),
                    new SimpleButton("stattier.clear", b -> TierCache.clearCache()),
                    new ScreenOpenButton("stattier.config.search", PlayerSearchScreen::new)
            };
        }
    }

    public class ColorsTab extends ButtonTab<TierTaggerConfig> {
        protected ColorsTab() {
            super("stattier.colors", TTConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
            Comparator<Map.Entry<String, Integer>> comparator = Comparator.comparing(e -> e.getKey().charAt(2));
            comparator = comparator.thenComparing(e -> e.getKey().charAt(0));

            List<ColorOption> tiers = config.getTierColors().entrySet().stream()
                    .sorted(comparator)
                    .map(e -> new ColorOption(e.getKey(), e.getValue(), val -> config.getTierColors().put(e.getKey(), val)))
                    .collect(Collectors.toList());

            tiers.addLast(new ColorOption("stattier.colors.retired", config.getRetiredColor(), config::setRetiredColor));

            return tiers.toArray(WidgetCreator[]::new);
        }
    }
}
