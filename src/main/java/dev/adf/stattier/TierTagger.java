package dev.adf.stattier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.adf.stattier.config.TierTaggerConfig;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.PlayerArgumentType;
import net.uku3lig.ukulib.utils.Ukutils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TierTagger implements ModInitializer {
    public static final String MOD_ID = "stattier";
    public static final Gson GSON = new GsonBuilder().create();

    @Getter
    private static final ConfigManager<TierTaggerConfig> manager = ConfigManager.createDefault(TierTaggerConfig.class, "stattier");
    @Getter
    private static final Logger logger = LoggerFactory.getLogger(TierTagger.class);
    @Getter
    private static final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void onInitialize() {
        TierCache.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) ->
                dispatcher.register(literal("stattier")
                        .then(argument("player", PlayerArgumentType.player())
                                .executes(TierTagger::displayTierInfo))));

        Ukutils.registerKeybinding(new KeyMapping("stattier.keybind.gamemode", -1, "stattier.name"),
                mc -> {
                    GameMode next = TierCache.findNextMode(manager.getConfig().getGameMode());
                    manager.getConfig().setGameMode(next.id());

                    if (mc.player != null) {
                        Component message = Component.literal("Displayed gamemode: ").append(next.asStyled(false));
                        mc.player.displayClientMessage(message, true);
                    }
                });
    }

    public static Component appendTier(String playerName, Component text) {
        MutableComponent following = getPlayerTier(playerName)
                .map(entry -> {
                    Component tierText = getRankingText(entry.ranking(), false);

                    if (manager.getConfig().isShowIcons() && entry.mode() != null && entry.mode().icon().isPresent()) {
                        return Component.literal(entry.mode().icon().get().toString()).append(tierText);
                    } else {
                        return tierText.copy();
                    }
                })
                .orElse(null);

        if (following != null) {
            following.append(Component.literal(" | ").withStyle(ChatFormatting.GRAY));
            return following.append(text);
        }

        return text;
    }

    public static Optional<PlayerInfo.NamedRanking> getPlayerTier(String playerName) {
        GameMode mode = manager.getConfig().getGameMode();

        return TierCache.getPlayerRankings(playerName)
                .map(rankings -> {
                    PlayerInfo.Ranking ranking = rankings.get(mode.id());
                    Optional<PlayerInfo.NamedRanking> highest = PlayerInfo.getHighestRanking(rankings);
                    TierTaggerConfig.HighestMode highestMode = manager.getConfig().getHighestMode();

                    if (ranking == null) {
                        if (highestMode != TierTaggerConfig.HighestMode.NEVER && highest.isPresent()) {
                            return highest.get();
                        } else {
                            return null;
                        }
                    } else {
                        if (highestMode == TierTaggerConfig.HighestMode.ALWAYS && highest.isPresent()) {
                            return highest.get();
                        } else {
                            return ranking.asNamed(mode);
                        }
                    }
                });
    }

    private static MutableComponent getTierText(int tier, int pos, boolean retired) {
        StringBuilder text = new StringBuilder();
        if (retired) text.append("R");
        text.append(pos == 0 ? "H" : "L").append("T").append(tier);

        int color = getTierColor(text.toString());
        return Component.literal(text.toString()).withStyle(s -> s.withColor(color));
    }

    public static Component getRankingText(PlayerInfo.Ranking ranking, boolean showPeak) {
        if (ranking.retired() && ranking.peakTier() != null && ranking.peakPos() != null) {
            return getTierText(ranking.peakTier(), ranking.peakPos(), true);
        } else {
            MutableComponent tierText = getTierText(ranking.tier(), ranking.pos(), false);

            if (showPeak && ranking.comparablePeak() < ranking.comparableTier()) {
                tierText.append(Component.literal(" (peak: ").withStyle(ChatFormatting.GRAY))
                        .append(getTierText(ranking.peakTier(), ranking.peakPos(), false))
                        .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
            }

            return tierText;
        }
    }

    private static int displayTierInfo(CommandContext<FabricClientCommandSource> ctx) {
        PlayerArgumentType.PlayerSelector selector = ctx.getArgument("player", PlayerArgumentType.PlayerSelector.class);
        String searchName = selector.name();

        Optional<String> worldPlayerName = ctx.getSource().getWorld().getPlayers().stream()
                .filter(p -> p.getScoreboardName().equalsIgnoreCase(searchName) || p.getStringUUID().equalsIgnoreCase(searchName))
                .findFirst()
                .map(p -> p.getScoreboardName());

        String lookupName = worldPlayerName.orElse(searchName);
        Optional<Map<String, PlayerInfo.Ranking>> rankings = TierCache.getPlayerRankings(lookupName);
        Optional<PlayerInfo> playerInfo = TierCache.getPlayerInfo(lookupName);

        if (playerInfo.isPresent() && rankings.isPresent()) {
            ctx.getSource().sendFeedback(printPlayerInfo(playerInfo.get()));
        } else {
            TierCache.searchPlayer(searchName).thenAccept(p ->
                    Minecraft.getInstance().execute(() -> {
                        if (p.rankings().isEmpty()) {
                            ctx.getSource().sendError(Component.literal("Player not found."));
                        } else {
                            ctx.getSource().sendFeedback(printPlayerInfo(p));
                        }
                    })
            ).exceptionally(t -> {
                ctx.getSource().sendError(Component.literal("Player not found."));
                return null;
            });
        }

        return 0;
    }

    private static Component printPlayerInfo(PlayerInfo info) {
        if (info.rankings().isEmpty()) {
            return Component.literal("Player not found.");
        }

        MutableComponent text = Component.empty().append("=== Rankings for " + info.name() + " ===");

        PlayerInfo.PointInfo pointInfo = info.getPointInfo();
        text.append(Component.literal("\n" + pointInfo.getTitle()).withStyle(s -> s.withColor(pointInfo.getColor())))
                .append(Component.literal(" (" + info.getTotalPoints() + " pts)").withStyle(ChatFormatting.GRAY));

        int rank = TierCache.getPlayerRank(info.name());
        if (rank > 0) {
            int rankColor = getRankColor(rank);
            text.append(Component.literal("\nRank: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("#" + rank).withStyle(s -> s.withColor(rankColor)));
        }

        info.rankings().forEach((m, r) -> {
            if (m == null) return;
            GameMode mode = TierCache.findModeOrUgly(m);
            Component tierText = getRankingText(r, true);
            text.append(Component.literal("\n").append(mode.asStyled(true)).append(": ").append(tierText));
        });

        return text;
    }

    public static int getRankColor(int rank) {
        if (rank == 1) return 0xFFF300;
        if (rank <= 3) return 0xDA9E20;
        if (rank <= 10) return 0xC0C0C0;
        if (rank <= 50) return 0xCD7F32;
        return 0x555555;
    }

    public static int getTierColor(String tier) {
        if (tier.startsWith("R")) {
            return manager.getConfig().getRetiredColor();
        } else {
            return manager.getConfig().getTierColors().getOrDefault(tier, 0xD3D3D3);
        }
    }
}
