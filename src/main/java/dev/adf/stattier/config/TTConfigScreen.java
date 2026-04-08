package dev.adf.stattier.config;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.tierlist.PlayerSearchScreen;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_7172;
import net.minecraft.class_7919;
import net.minecraft.class_8087;
import net.uku3lig.ukulib.config.option.ColorOption;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.ScreenOpenButton;
import net.uku3lig.ukulib.config.option.SimpleButton;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.option.widget.ButtonTab;
import net.uku3lig.ukulib.config.screen.TabbedConfigScreen;

public class TTConfigScreen extends TabbedConfigScreen<TierTaggerConfig> {
   public TTConfigScreen(class_437 parent) {
      super("StatTier Config", parent, TierTagger.getManager());
   }

   protected class_8087[] getTabs(TierTaggerConfig config) {
      return new class_8087[]{new TTConfigScreen.MainSettingsTab(this), new TTConfigScreen.ColorsTab(this)};
   }

   public class MainSettingsTab extends ButtonTab<TierTaggerConfig> {
      public MainSettingsTab(final TTConfigScreen this$0) {
         super("stattier.config", this$0.manager);
      }

      protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
         WidgetCreator[] var10000 = new WidgetCreator[8];
         boolean var10004 = config.isEnabled();
         Objects.requireNonNull(config);
         var10000[0] = CyclingOption.ofBoolean("stattier.config.enabled", var10004, config::setEnabled);
         var10000[1] = new CyclingOption<>("stattier.config.gamemode", TierCache.getGamemodes(), config.getGameMode(), (GameMode m) -> {
            config.setGameMode(m.id());
         }, (GameMode m) -> {
            return class_2561.method_43470(m.title());
         }, (GameMode m) -> {
            return m.isNone() ? class_7919.method_47407(class_2561.method_43471("stattier.config.gamemode.none")) : null;
         });
         var10004 = config.isShowRetired();
         Objects.requireNonNull(config);
         var10000[2] = CyclingOption.ofBoolean("stattier.config.retired", var10004, config::setShowRetired);
         TierTaggerConfig.HighestMode var10005 = config.getHighestMode();
         Objects.requireNonNull(config);
         var10000[3] = CyclingOption.ofTranslatableEnum("stattier.config.highest", TierTaggerConfig.HighestMode.class, var10005, config::setHighestMode, class_7172.method_42717(class_2561.method_43471("stattier.config.highest.desc")));
         var10004 = config.isShowIcons();
         Objects.requireNonNull(config);
         var10000[4] = CyclingOption.ofBoolean("stattier.config.icons", var10004, config::setShowIcons);
         var10004 = config.isPlayerList();
         Objects.requireNonNull(config);
         var10000[5] = CyclingOption.ofBoolean("stattier.config.playerList", var10004, config::setPlayerList);
         var10000[6] = new SimpleButton("stattier.clear", (b) -> {
            TierCache.clearCache();
         });
         var10000[7] = new ScreenOpenButton("stattier.config.search", PlayerSearchScreen::new);
         return var10000;
      }
   }

   public class ColorsTab extends ButtonTab<TierTaggerConfig> {
      protected ColorsTab(final TTConfigScreen this$0) {
         super("stattier.colors", this$0.manager);
      }

      protected WidgetCreator[] getWidgets(TierTaggerConfig config) {
         Comparator<Entry<String, Integer>> comparator = Comparator.comparing((e) -> {
            return ((String)e.getKey()).charAt(2);
         });
         comparator = comparator.thenComparing((e) -> {
            return ((String)e.getKey()).charAt(0);
         });
         List<ColorOption> tiers = (List)config.getTierColors().entrySet().stream().sorted(comparator).map((e) -> {
            return new ColorOption((String)e.getKey(), (Integer)e.getValue(), (val) -> {
               config.getTierColors().put((String)e.getKey(), val);
            });
         }).collect(Collectors.toList());
         int var10004 = config.getRetiredColor();
         Objects.requireNonNull(config);
         tiers.addLast(new ColorOption("stattier.colors.retired", var10004, config::setRetiredColor));
         return (WidgetCreator[])tiers.toArray((x$0) -> {
            return new WidgetCreator[x$0];
         });
      }
   }
}
