package dev.adf.stattier.model;

import dev.adf.stattier.TierCache;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record PlayerInfo(String discordId, String name, Map<String, PlayerInfo.Ranking> rankings, String region, boolean inServer) {
   private static final Map<String, Integer> REGION_COLORS = Map.of("NA", 16738926, "EU", 7012206, "SA", 16750848, "AU", 16167531, "ME", 16767334, "AS", 12745632, "AF", 6770343);

   public PlayerInfo(String discordId, String name, Map<String, PlayerInfo.Ranking> rankings, String region, boolean inServer) {
      this.discordId = discordId;
      this.name = name;
      this.rankings = rankings;
      this.region = region;
      this.inServer = inServer;
   }

   public int getTotalPoints() {
      return this.rankings.values().stream().mapToInt(PlayerInfo.Ranking::getPoints).sum();
   }

   public PlayerInfo.PointInfo getPointInfo() {
      int points = getTotalPoints();
      if (points >= 400) return PointInfo.COMBAT_GRANDMASTER;
      if (points >= 250) return PointInfo.COMBAT_MASTER;
      if (points >= 100) return PointInfo.COMBAT_ACE;
      if (points >= 50) return PointInfo.COMBAT_SPECIALIST;
      if (points >= 20) return PointInfo.COMBAT_CADET;
      if (points >= 10) return PointInfo.COMBAT_NOVICE;
      return PointInfo.ROOKIE;
   }

   public int getRegionColor() {
      return (Integer)REGION_COLORS.getOrDefault(this.region.toUpperCase(Locale.ROOT), 16777215);
   }

   public static Optional<PlayerInfo.NamedRanking> getHighestRanking(Map<String, PlayerInfo.Ranking> rankings) {
      return rankings.entrySet().stream().filter((e) -> {
         return e.getKey() != null;
      }).min(Comparator.comparingInt((e) -> {
         return ((PlayerInfo.Ranking)e.getValue()).comparableTier();
      })).map((e) -> {
         return ((PlayerInfo.Ranking)e.getValue()).asNamed(TierCache.findModeOrUgly((String)e.getKey()));
      });
   }

   public List<PlayerInfo.NamedRanking> getSortedTiers() {
      List<PlayerInfo.NamedRanking> tiers = new ArrayList(this.rankings.entrySet().stream().map((e) -> {
         return ((PlayerInfo.Ranking)e.getValue()).asNamed(TierCache.findModeOrUgly((String)e.getKey()));
      }).toList());
      tiers.sort(Comparator.comparing((PlayerInfo.NamedRanking a) -> {
         return a.ranking().retired();
      }, Boolean::compare).thenComparingInt((PlayerInfo.NamedRanking a) -> {
         return a.ranking().tier();
      }).thenComparingInt((PlayerInfo.NamedRanking a) -> {
         return a.ranking().pos();
      }));
      return tiers;
   }

   public String discordId() {
      return this.discordId;
   }

   public String name() {
      return this.name;
   }

   public Map<String, PlayerInfo.Ranking> rankings() {
      return this.rankings;
   }

   public String region() {
      return this.region;
   }

   public boolean inServer() {
      return this.inServer;
   }

   public static record NamedRanking(@Nullable GameMode mode, PlayerInfo.Ranking ranking) {
      public NamedRanking(@Nullable GameMode mode, PlayerInfo.Ranking ranking) {
         this.mode = mode;
         this.ranking = ranking;
      }

      @Nullable
      public GameMode mode() {
         return this.mode;
      }

      public PlayerInfo.Ranking ranking() {
         return this.ranking;
      }
   }

   public static record Ranking(int tier, int pos, @Nullable Integer peakTier, @Nullable Integer peakPos, long attained, boolean retired) {
      public Ranking(int tier, int pos, @Nullable Integer peakTier, @Nullable Integer peakPos, long attained, boolean retired) {
         this.tier = tier;
         this.pos = pos;
         this.peakTier = peakTier;
         this.peakPos = peakPos;
         this.attained = attained;
         this.retired = retired;
      }

      public int comparableTier() {
         return this.tier * 2 + this.pos;
      }

      public int comparablePeak() {
         return this.peakTier != null && this.peakPos != null ? this.peakTier * 2 + this.peakPos : Integer.MAX_VALUE;
      }

      public PlayerInfo.NamedRanking asNamed(GameMode mode) {
         return new PlayerInfo.NamedRanking(mode, this);
      }

      public int tier() {
         return this.tier;
      }

      public int pos() {
         return this.pos;
      }

      @Nullable
      public Integer peakTier() {
         return this.peakTier;
      }

      @Nullable
      public Integer peakPos() {
         return this.peakPos;
      }

      public long attained() {
         return this.attained;
      }

      public boolean retired() {
         return this.retired;
      }

      public int getPoints() {
         switch (this.tier) {
            case 1: return this.pos == 0 ? 60 : 45;
            case 2: return this.pos == 0 ? 30 : 20;
            case 3: return this.pos == 0 ? 10 : 6;
            case 4: return this.pos == 0 ? 4 : 3;
            case 5: return this.pos == 0 ? 2 : 1;
            default: return 0;
         }
      }
   }

   public static enum PointInfo {
      COMBAT_GRANDMASTER("\u265A Combat Grandmaster", 16638023),
      COMBAT_MASTER("Combat Master", 16638023),
      COMBAT_ACE("Combat Ace", 16622767),
      COMBAT_SPECIALIST("Combat Specialist", 14202110),
      COMBAT_CADET("Combat Cadet", 12891645),
      COMBAT_NOVICE("Combat Novice", 12891645),
      ROOKIE("Rookie", 11184810);

      private final String title;
      private final int color;

      public String getTitle() {
         return this.title;
      }

      public int getColor() {
         return this.color;
      }

      private PointInfo(final String title, final int color) {
         this.title = title;
         this.color = color;
      }
   }

   public static record Badge(String title, String desc) {
      public Badge(String title, String desc) {
         this.title = title;
         this.desc = desc;
      }

      public String title() {
         return this.title;
      }

      public String desc() {
         return this.desc;
      }
   }
}
