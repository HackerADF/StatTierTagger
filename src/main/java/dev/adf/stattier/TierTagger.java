package dev.adf.stattier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.adf.stattier.config.TierTaggerConfig;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.PlayerArgumentType;
import net.uku3lig.ukulib.utils.Ukutils;
import net.uku3lig.ukulib.utils.PlayerArgumentType.PlayerSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TierTagger implements ModInitializer {
   public static final String MOD_ID = "stattier";
   public static final Gson GSON = (new GsonBuilder()).create();
   private static final ConfigManager<TierTaggerConfig> manager = ConfigManager.createDefault(TierTaggerConfig.class, "stattier");
   private static final Logger logger = LoggerFactory.getLogger(TierTagger.class);
   private static final HttpClient client = HttpClient.newHttpClient();

   public void onInitialize() {
      TierCache.init();
      ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
         dispatcher.register((LiteralArgumentBuilder)ClientCommandManager.literal("stattier").then(ClientCommandManager.argument("player", PlayerArgumentType.player()).executes(TierTagger::displayTierInfo)));
      });
      Ukutils.registerKeybinding(new class_304("stattier.keybind.gamemode", -1, "stattier.name"), (mc) -> {
         GameMode next = TierCache.findNextMode(((TierTaggerConfig)manager.getConfig()).getGameMode());
         ((TierTaggerConfig)manager.getConfig()).setGameMode(next.id());
         if (mc.field_1724 != null) {
            class_2561 message = class_2561.method_43470("Displayed gamemode: ").method_10852(next.asStyled(false));
            mc.field_1724.method_7353(message, true);
         }

      });
   }

   public static class_2561 appendTier(String playerName, class_2561 text) {
      class_5250 following = (class_5250)getPlayerTier(playerName).map((entry) -> {
         class_2561 tierText = getRankingText(entry.ranking(), false);
         return ((TierTaggerConfig)manager.getConfig()).isShowIcons() && entry.mode() != null && entry.mode().icon().isPresent() ? class_2561.method_43470(((Character)entry.mode().icon().get()).toString()).method_10852(tierText) : tierText.method_27661();
      }).orElse(null);
      if (following != null) {
         following.method_10852(class_2561.method_43470(" | ").method_27692(class_124.field_1080));
         return following.method_10852(text);
      } else {
         return text;
      }
   }

   public static Optional<PlayerInfo.NamedRanking> getPlayerTier(String playerName) {
      GameMode mode = ((TierTaggerConfig)manager.getConfig()).getGameMode();
      return TierCache.getPlayerRankings(playerName).map((rankings) -> {
         PlayerInfo.Ranking ranking = (PlayerInfo.Ranking)rankings.get(mode.id());
         Optional<PlayerInfo.NamedRanking> highest = PlayerInfo.getHighestRanking(rankings);
         TierTaggerConfig.HighestMode highestMode = ((TierTaggerConfig)manager.getConfig()).getHighestMode();
         if (ranking == null) {
            return highestMode != TierTaggerConfig.HighestMode.NEVER && highest.isPresent() ? (PlayerInfo.NamedRanking)highest.get() : null;
         } else {
            return highestMode == TierTaggerConfig.HighestMode.ALWAYS && highest.isPresent() ? (PlayerInfo.NamedRanking)highest.get() : ranking.asNamed(mode);
         }
      });
   }

   private static class_5250 getTierText(int tier, int pos, boolean retired) {
      StringBuilder text = new StringBuilder();
      if (retired) {
         text.append("R");
      }

      text.append(pos == 0 ? "H" : "L").append("T").append(tier);
      int color = getTierColor(text.toString());
      return class_2561.method_43470(text.toString()).method_27694((s) -> {
         return s.method_36139(color);
      });
   }

   public static class_2561 getRankingText(PlayerInfo.Ranking ranking, boolean showPeak) {
      if (ranking.retired() && ranking.peakTier() != null && ranking.peakPos() != null) {
         return getTierText(ranking.peakTier(), ranking.peakPos(), true);
      } else {
         class_5250 tierText = getTierText(ranking.tier(), ranking.pos(), false);
         if (showPeak && ranking.comparablePeak() < ranking.comparableTier()) {
            tierText.method_10852(class_2561.method_43470(" (peak: ").method_27694((s) -> {
               return s.method_10977(class_124.field_1080);
            })).method_10852(getTierText(ranking.peakTier(), ranking.peakPos(), false)).method_10852(class_2561.method_43470(")").method_27694((s) -> {
               return s.method_10977(class_124.field_1080);
            }));
         }

         return tierText;
      }
   }

   private static int displayTierInfo(CommandContext<FabricClientCommandSource> ctx) {
      PlayerSelector selector = (PlayerSelector)ctx.getArgument("player", PlayerSelector.class);
      String searchName = selector.name();

      Optional<String> worldPlayerName = ((FabricClientCommandSource)ctx.getSource()).getWorld().method_18456().stream().filter((p) -> {
         return p.method_5820().equalsIgnoreCase(searchName) || p.method_5845().equalsIgnoreCase(searchName);
      }).findFirst().map((p) -> p.method_5820());

      String lookupName = worldPlayerName.orElse(searchName);
      Optional<Map<String, PlayerInfo.Ranking>> rankings = TierCache.getPlayerRankings(lookupName);

      Optional<PlayerInfo> playerInfo = TierCache.getPlayerInfo(lookupName);
      if (playerInfo.isPresent() && rankings.isPresent()) {
         ((FabricClientCommandSource)ctx.getSource()).sendFeedback(printPlayerInfo(playerInfo.get()));
      } else {
         TierCache.searchPlayer(searchName).thenAccept((p) -> {
            class_310.method_1551().execute(() -> {
               if (p.rankings().isEmpty()) {
                  ((FabricClientCommandSource)ctx.getSource()).sendError(class_2561.method_30163("Player not found."));
               } else {
                  ((FabricClientCommandSource)ctx.getSource()).sendFeedback(printPlayerInfo(p));
               }
            });
         }).exceptionally((t) -> {
            ((FabricClientCommandSource)ctx.getSource()).sendError(class_2561.method_30163("Player not found."));
            return null;
         });
      }

      return 0;
   }

   private static class_2561 printPlayerInfo(PlayerInfo info) {
      if (info.rankings().isEmpty()) {
         return class_2561.method_43470("Player not found.");
      } else {
         class_5250 text = class_2561.method_43473().method_27693("=== Rankings for " + info.name() + " ===");
         PlayerInfo.PointInfo pointInfo = info.getPointInfo();
         text.method_10852(class_2561.method_43470("\n" + pointInfo.getTitle()).method_27694((s) -> {
            return s.method_36139(pointInfo.getColor());
         })).method_10852(class_2561.method_43470(" (" + info.getTotalPoints() + " pts)").method_27692(class_124.field_1080));
         int rank = TierCache.getPlayerRank(info.name());
         if (rank > 0) {
            int rankColor = getRankColor(rank);
            text.method_10852(class_2561.method_43470("\nRank: ").method_27692(class_124.field_1080)).method_10852(class_2561.method_43470("#" + rank).method_27694((s) -> {
               return s.method_36139(rankColor);
            }));
         }
         info.rankings().forEach((m, r) -> {
            if (m != null) {
               GameMode mode = TierCache.findModeOrUgly(m);
               class_2561 tierText = getRankingText(r, true);
               text.method_10852(class_2561.method_43470("\n").method_10852(mode.asStyled(true)).method_27693(": ").method_10852(tierText));
            }
         });
         return text;
      }
   }

   public static int getRankColor(int rank) {
      if (rank == 1) return 16763904;
      if (rank <= 3) return 14329120;
      if (rank <= 10) return 12632256;
      if (rank <= 50) return 13467442;
      return 5592405;
   }

   public static int getTierColor(String tier) {
      return tier.startsWith("R") ? ((TierTaggerConfig)manager.getConfig()).getRetiredColor() : (Integer)((TierTaggerConfig)manager.getConfig()).getTierColors().getOrDefault(tier, 13882323);
   }

public static ConfigManager<TierTaggerConfig> getManager() {
      return manager;
   }

public static Logger getLogger() {
      return logger;
   }

public static HttpClient getClient() {
      return client;
   }
}
