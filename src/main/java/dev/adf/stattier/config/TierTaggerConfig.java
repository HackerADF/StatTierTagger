package dev.adf.stattier.config;

import com.google.gson.internal.LinkedTreeMap;
import dev.adf.stattier.TierCache;
import dev.adf.stattier.model.GameMode;
import java.io.Serializable;
import java.util.Optional;
import net.minecraft.class_7291;

public class TierTaggerConfig implements Serializable {
   private boolean enabled = true;
   private String gameMode = "sword";
   private boolean showRetired = true;
   private TierTaggerConfig.HighestMode highestMode;
   private boolean showIcons;
   private boolean playerList;
   private int retiredColor;
   private LinkedTreeMap<String, Integer> tierColors;
   private String apiUrl;

   public GameMode getGameMode() {
      Optional<GameMode> opt = TierCache.findMode(this.gameMode);
      if (opt.isPresent()) {
         return (GameMode)opt.get();
      } else {
         GameMode first = (GameMode)TierCache.getGamemodes().getFirst();
         if (!first.isNone()) {
            this.gameMode = first.id();
         }

         return first;
      }
   }

   private static LinkedTreeMap<String, Integer> defaultColors() {
      LinkedTreeMap<String, Integer> colors = new LinkedTreeMap();
      colors.put("HT1", 15252026);
      colors.put("LT1", 14005077);
      colors.put("HT2", 12899303);
      colors.put("LT2", 10528690);
      colors.put("HT3", 16293722);
      colors.put("LT3", 13007682);
      colors.put("HT4", 8483994);
      colors.put("LT4", 6642553);
      colors.put("HT5", 9405096);
      colors.put("LT5", 6642553);
      return colors;
   }

public boolean isEnabled() {
      return this.enabled;
   }

public boolean isShowRetired() {
      return this.showRetired;
   }

public TierTaggerConfig.HighestMode getHighestMode() {
      return this.highestMode;
   }

public boolean isShowIcons() {
      return this.showIcons;
   }

public boolean isPlayerList() {
      return this.playerList;
   }

public int getRetiredColor() {
      return this.retiredColor;
   }

public LinkedTreeMap<String, Integer> getTierColors() {
      return this.tierColors;
   }

public String getApiUrl() {
      return this.apiUrl;
   }

public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

public void setGameMode(String gameMode) {
      this.gameMode = gameMode;
   }

public void setShowRetired(boolean showRetired) {
      this.showRetired = showRetired;
   }

public void setHighestMode(TierTaggerConfig.HighestMode highestMode) {
      this.highestMode = highestMode;
   }

public void setShowIcons(boolean showIcons) {
      this.showIcons = showIcons;
   }

public void setPlayerList(boolean playerList) {
      this.playerList = playerList;
   }

public void setRetiredColor(int retiredColor) {
      this.retiredColor = retiredColor;
   }

public void setTierColors(LinkedTreeMap<String, Integer> tierColors) {
      this.tierColors = tierColors;
   }

public void setApiUrl(String apiUrl) {
      this.apiUrl = apiUrl;
   }

public TierTaggerConfig() {
      this.highestMode = TierTaggerConfig.HighestMode.NOT_FOUND;
      this.showIcons = true;
      this.playerList = true;
      this.retiredColor = 10671871;
      this.tierColors = defaultColors();
      this.apiUrl = "https://shrill-mode-ce7d.urbruh122.workers.dev";
   }

public TierTaggerConfig(boolean enabled, String gameMode, boolean showRetired, TierTaggerConfig.HighestMode highestMode, boolean showIcons, boolean playerList, int retiredColor, LinkedTreeMap<String, Integer> tierColors, String apiUrl) {
      this.highestMode = TierTaggerConfig.HighestMode.NOT_FOUND;
      this.showIcons = true;
      this.playerList = true;
      this.retiredColor = 10671871;
      this.tierColors = defaultColors();
      this.apiUrl = "https://shrill-mode-ce7d.urbruh122.workers.dev";
      this.enabled = enabled;
      this.gameMode = gameMode;
      this.showRetired = showRetired;
      this.highestMode = highestMode;
      this.showIcons = showIcons;
      this.playerList = playerList;
      this.retiredColor = retiredColor;
      this.tierColors = tierColors;
      this.apiUrl = apiUrl;
   }

   public static enum HighestMode implements class_7291 {
      NEVER(0, "stattier.highest.never"),
      NOT_FOUND(1, "stattier.highest.not_found"),
      ALWAYS(2, "stattier.highest.always");

      private final int id;
      private final String translationKey;

      public int method_7362() {
         return this.id;
      }

      public String method_7359() {
         return this.translationKey;
      }

      private HighestMode(final int id, final String translationKey) {
         this.id = id;
         this.translationKey = translationKey;
      }

      private static TierTaggerConfig.HighestMode[] $values() {
         return new TierTaggerConfig.HighestMode[]{NEVER, NOT_FOUND, ALWAYS};
      }
   }
}
