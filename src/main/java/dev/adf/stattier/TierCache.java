package dev.adf.stattier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TierCache {
   private static final List<GameMode> GAMEMODES = new ArrayList();
   private static final Map<String, PlayerInfo> PLAYERS = new ConcurrentHashMap();
   private static final Map<String, Map<String, PlayerInfo.Ranking>> TIERS = new ConcurrentHashMap();
   private static final Map<String, Integer> RANKS = new ConcurrentHashMap();

   public static void init() {
      fetchLeaderboard();
   }

   private static void fetchLeaderboard() {
      String endpoint = ((dev.adf.stattier.config.TierTaggerConfig)TierTagger.getManager().getConfig()).getApiUrl() + "/leaderboard";
      HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).GET().build();

      try {
         TierTagger.getClient().sendAsync(request, BodyHandlers.ofString()).thenAccept((response) -> {
            JsonArray array = (JsonArray)TierTagger.GSON.fromJson((String)response.body(), JsonArray.class);
            Set<String> modeSet = new LinkedHashSet();
            PLAYERS.clear();
            TIERS.clear();
            for (JsonElement element : array) {
               JsonObject obj = element.getAsJsonObject();
               String username = obj.get("username").getAsString();
               String region = obj.has("region") && !obj.get("region").isJsonNull() ? obj.get("region").getAsString() : "";
               String discordId = obj.has("discord_id") && !obj.get("discord_id").isJsonNull() ? obj.get("discord_id").getAsString() : "";
               boolean inServer = obj.has("in_server") && obj.get("in_server").getAsBoolean();
               Map<String, PlayerInfo.Ranking> rankings = new HashMap();

               if (obj.has("tiers") && !obj.get("tiers").isJsonNull() && obj.get("tiers").isJsonObject()) {
                  JsonObject tiers = obj.getAsJsonObject("tiers");

                  for (Map.Entry<String, JsonElement> entry : tiers.entrySet()) {
                     String modeId = ((String)entry.getKey()).toLowerCase(Locale.ROOT);
                     String tierString = ((JsonElement)entry.getValue()).getAsString();
                     PlayerInfo.Ranking ranking = parseTierString(tierString);
                     if (ranking != null) {
                        rankings.put(modeId, ranking);
                        modeSet.add(modeId + ":" + (String)entry.getKey());
                     }
                  }
               }

               String key = username.toLowerCase(Locale.ROOT);
               TIERS.put(key, rankings);
               PLAYERS.put(key, new PlayerInfo(discordId, username, rankings, region, inServer));
            }

            GAMEMODES.clear();

            for (String modeEntry : modeSet) {
               String[] parts = modeEntry.split(":", 2);
               GAMEMODES.add(new GameMode(parts[0], parts[1]));
            }

            RANKS.clear();
            List<Map.Entry<String, PlayerInfo>> ranked = new ArrayList();
            for (Map.Entry<String, PlayerInfo> e : PLAYERS.entrySet()) {
               if (!((PlayerInfo)e.getValue()).rankings().isEmpty()) {
                  ranked.add(e);
               }
            }
            ranked.sort(Comparator.comparingInt((Map.Entry<String, PlayerInfo> e) -> ((PlayerInfo)e.getValue()).getTotalPoints()).reversed());
            for (int i = 0; i < ranked.size(); i++) {
               RANKS.put((String)((Map.Entry)ranked.get(i)).getKey(), i + 1);
            }

            TierTagger.getLogger().info("Loaded {} players and {} game modes from leaderboard", PLAYERS.size(), GAMEMODES.size());
         }).get();
      } catch (Exception var2) {
         TierTagger.getLogger().error("Failed to load leaderboard!", var2);
      }
   }

   private static PlayerInfo.Ranking parseTierString(String tierString) {
      try {
         String[] parts = tierString.split(" ");
         if (parts.length != 3) {
            return null;
         }

         int pos = parts[0].equalsIgnoreCase("High") ? 0 : 1;
         int tier = Integer.parseInt(parts[2]);
         return new PlayerInfo.Ranking(tier, pos, (Integer)null, (Integer)null, 0L, false);
      } catch (Exception var4) {
         TierTagger.getLogger().warn("Could not parse tier string: {}", tierString);
         return null;
      }
   }

   public static List<GameMode> getGamemodes() {
      return GAMEMODES.isEmpty() ? Collections.singletonList(GameMode.NONE) : GAMEMODES;
   }

   public static Optional<Map<String, PlayerInfo.Ranking>> getPlayerRankings(String name) {
      Map<String, PlayerInfo.Ranking> rankings = (Map)TIERS.get(name.toLowerCase(Locale.ROOT));
      if (rankings != null && rankings.isEmpty()) {
         return Optional.empty();
      }

      return Optional.ofNullable(rankings);
   }

   public static Optional<Map<String, PlayerInfo.Ranking>> getPlayerRankingsByUuid(java.util.UUID uuid) {
      return Optional.empty();
   }

   public static Optional<PlayerInfo> getPlayerInfo(String name) {
      return Optional.ofNullable((PlayerInfo)PLAYERS.get(name.toLowerCase(Locale.ROOT)));
   }

   public static int getPlayerRank(String name) {
      return (Integer)RANKS.getOrDefault(name.toLowerCase(Locale.ROOT), 0);
   }

   public static CompletableFuture<PlayerInfo> searchPlayer(String query) {
      return CompletableFuture.supplyAsync(() -> {
         String key = query.toLowerCase(Locale.ROOT);
         PlayerInfo info = (PlayerInfo)PLAYERS.get(key);
         if (info != null) {
            return info;
         }

         for (Map.Entry<String, PlayerInfo> entry : PLAYERS.entrySet()) {
            if (((String)entry.getKey()).contains(key)) {
               return (PlayerInfo)entry.getValue();
            }
         }

         throw new RuntimeException("Player not found in leaderboard: " + query);
      });
   }

   public static void clearCache() {
      TIERS.clear();
      PLAYERS.clear();
      RANKS.clear();
      init();
   }

   public static GameMode findNextMode(GameMode current) {
      return GAMEMODES.isEmpty() ? GameMode.NONE : (GameMode)GAMEMODES.get((GAMEMODES.indexOf(current) + 1) % GAMEMODES.size());
   }

   public static Optional<GameMode> findMode(String id) {
      return GAMEMODES.stream().filter((m) -> {
         return m.id().equalsIgnoreCase(id);
      }).findFirst();
   }

   public static GameMode findModeOrUgly(String id) {
      return (GameMode)findMode(id).orElseGet(() -> {
         return new GameMode(id, id);
      });
   }

   private TierCache() {
   }
}
