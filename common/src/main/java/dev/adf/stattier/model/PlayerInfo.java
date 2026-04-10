package dev.adf.stattier.model;

import dev.adf.stattier.TierCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record PlayerInfo(String discordId, String name, Map<String, Ranking> rankings, String region, boolean inServer) {
    private static final Map<String, Integer> REGION_COLORS = Map.of(
            "NA", 0xff6a6e,
            "EU", 0x6aff6e,
            "SA", 0xff9900,
            "AU", 0xf6b26b,
            "ME", 0xffd966,
            "AS", 0xc27ba0,
            "AF", 0x674ea7
    );

    public int getTotalPoints() {
        return this.rankings.values().stream().mapToInt(Ranking::getPoints).sum();
    }

    public PointInfo getPointInfo() {
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
        return REGION_COLORS.getOrDefault(this.region.toUpperCase(Locale.ROOT), 0xffffff);
    }

    public static Optional<NamedRanking> getHighestRanking(Map<String, Ranking> rankings) {
        return rankings.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .min(Comparator.comparingInt(e -> e.getValue().comparableTier()))
                .map(e -> e.getValue().asNamed(TierCache.findModeOrUgly(e.getKey())));
    }

    public List<NamedRanking> getSortedTiers() {
        List<NamedRanking> tiers = new ArrayList<>(this.rankings.entrySet().stream()
                .map(e -> e.getValue().asNamed(TierCache.findModeOrUgly(e.getKey())))
                .toList());

        tiers.sort(Comparator.comparing((NamedRanking a) -> a.ranking.retired, Boolean::compare)
                .thenComparingInt(a -> a.ranking.tier)
                .thenComparingInt(a -> a.ranking.pos));

        return tiers;
    }

    public record NamedRanking(@Nullable GameMode mode, Ranking ranking) {
    }

    public record Ranking(int tier, int pos, @Nullable Integer peakTier, @Nullable Integer peakPos, long attained, boolean retired) {
        public int comparableTier() {
            return tier * 2 + pos;
        }

        public int comparablePeak() {
            if (peakTier == null || peakPos == null) {
                return Integer.MAX_VALUE;
            } else {
                return peakTier * 2 + peakPos;
            }
        }

        public NamedRanking asNamed(GameMode mode) {
            return new NamedRanking(mode, this);
        }

        public int getPoints() {
            return switch (this.tier) {
                case 1 -> this.pos == 0 ? 60 : 45;
                case 2 -> this.pos == 0 ? 30 : 20;
                case 3 -> this.pos == 0 ? 10 : 6;
                case 4 -> this.pos == 0 ? 4 : 3;
                case 5 -> this.pos == 0 ? 2 : 1;
                default -> 0;
            };
        }
    }

    @Getter
    @AllArgsConstructor
    public enum PointInfo {
        COMBAT_GRANDMASTER("\u265A Combat Grandmaster", 0xFE1617),
        COMBAT_MASTER("Combat Master", 0xFE1617),
        COMBAT_ACE("Combat Ace", 0xFD8F4F),
        COMBAT_SPECIALIST("Combat Specialist", 0xD8BA7E),
        COMBAT_CADET("Combat Cadet", 0xC4B1BD),
        COMBAT_NOVICE("Combat Novice", 0xC4B1BD),
        ROOKIE("Rookie", 0xAAAAAA);

        private final String title;
        private final int color;
    }

    public record Badge(String title, String desc) {
    }
}
