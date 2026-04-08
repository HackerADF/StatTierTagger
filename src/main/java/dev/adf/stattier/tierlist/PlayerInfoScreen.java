package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7842;
import net.minecraft.class_8765;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import org.jetbrains.annotations.NotNull;

public class PlayerInfoScreen extends CloseableScreen {
   private static final Map<String, Integer> REGION_COLORS = Map.of("NA", 16738926, "EU", 7012206, "SA", 16750848, "AU", 16167531, "ME", 16767334, "AS", 12745632, "AF", 6770343);
   private final PlayerInfo info;
   private final class_8765 skin;

   public PlayerInfoScreen(class_437 parent, PlayerInfo info, class_8765 skin) {
      super(class_2561.method_30163("Player Info"), parent);
      this.info = info;
      this.skin = skin;
   }

   protected void method_25426() {
      this.method_37063(class_4185.method_46430(class_5244.field_24334, (button) -> {
         class_310.method_1551().method_1507(this.parent);
      }).method_46434(this.field_22789 / 2 - 100, this.field_22790 - 27, 200, 20).method_46431());
      this.method_37063(this.skin);
      int rankingHeight = this.info.rankings().size() * 11;
      int rank = TierCache.getPlayerRank(this.info.name());
      int infoHeight = rank > 0 ? 60 : 45;
      int startY = (this.field_22790 - infoHeight - rankingHeight) / 2;
      int rankingY = startY + infoHeight;
      Iterator var5 = this.info.getSortedTiers().iterator();

      while(var5.hasNext()) {
         PlayerInfo.NamedRanking namedRanking = (PlayerInfo.NamedRanking)var5.next();
         if (namedRanking.mode() != null) {
            class_7842 text = new class_7842(this.formatTier(namedRanking.mode(), namedRanking.ranking()), this.field_22793);
            text.method_46421(this.field_22789 / 2 + 5);
            text.method_46419(rankingY);
            this.method_37063(text);
            rankingY += 11;
         }
      }

   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(this.field_22793, this.info.name() + "'s profile", this.field_22789 / 2, 20, -1);
      int rankingHeight = this.info.rankings().size() * 11;
      int rank = TierCache.getPlayerRank(this.info.name());
      int infoHeight = rank > 0 ? 60 : 45;
      int startY = (this.field_22790 - infoHeight - rankingHeight) / 2;
      context.method_27535(this.field_22793, this.getRegionText(this.info), this.field_22789 / 2 + 5, startY, -1);
      context.method_27535(this.field_22793, this.getTitleText(this.info), this.field_22789 / 2 + 5, startY + 15, -1);
      if (rank > 0) {
         context.method_27535(this.field_22793, this.getRankText(rank), this.field_22789 / 2 + 5, startY + 30, -1);
         context.method_25303(this.field_22793, "Rankings:", this.field_22789 / 2 + 5, startY + 45, -1);
      } else {
         context.method_25303(this.field_22793, "Rankings:", this.field_22789 / 2 + 5, startY + 30, -1);
      }
   }

   private class_2561 formatTier(@NotNull GameMode gamemode, PlayerInfo.Ranking ranking) {
      class_2561 tierText = TierTagger.getRankingText(ranking, false);
      return class_2561.method_43473().method_10852(gamemode.asStyled(true)).method_10852(class_2561.method_43470(": ").method_27692(class_124.field_1080)).method_10852(tierText);
   }

   private class_2561 getTitleText(PlayerInfo info) {
      PlayerInfo.PointInfo pointInfo = info.getPointInfo();
      return class_2561.method_43473().method_10852(class_2561.method_43470(pointInfo.getTitle()).method_27694((s) -> {
         return s.method_36139(pointInfo.getColor());
      })).method_10852(class_2561.method_43470(" (" + info.getTotalPoints() + " pts)").method_27692(class_124.field_1080));
   }

   private class_2561 getRankText(int rank) {
      int rankColor = TierTagger.getRankColor(rank);
      return class_2561.method_43473().method_10852(class_2561.method_43470("Rank: ").method_27692(class_124.field_1080)).method_10852(class_2561.method_43470("#" + rank).method_27694((s) -> {
         return s.method_36139(rankColor);
      }));
   }

   private class_2561 getRegionText(PlayerInfo info) {
      int color = (Integer)REGION_COLORS.getOrDefault(info.region().toUpperCase(Locale.ROOT), 16777215);
      return class_2561.method_43473().method_10852(class_2561.method_43470("Region: ")).method_10852(class_2561.method_43470(info.region()).method_27694((s) -> {
         return s.method_36139(color);
      }));
   }
}
