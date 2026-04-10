package dev.adf.stattier;

import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.utils.PlayerArgumentType;
import net.uku3lig.ukulib.utils.Ukutils;

import java.util.Map;
import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class StatTierFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TierTagger.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) ->
                dispatcher.register(literal("stattier")
                        .then(argument("player", PlayerArgumentType.player())
                                .executes(StatTierFabric::displayTierInfo))));

        Ukutils.registerKeybinding(new KeyMapping("stattier.keybind.gamemode", -1, "stattier.name"),
                mc -> {
                    GameMode next = TierCache.findNextMode(TierTagger.getManager().getConfig().getGameMode());
                    TierTagger.getManager().getConfig().setGameMode(next.id());

                    if (mc.player != null) {
                        Component message = Component.literal("Displayed gamemode: ").append(next.asStyled(false));
                        mc.player.displayClientMessage(message, true);
                    }
                });
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
            ctx.getSource().sendFeedback(TierTagger.printPlayerInfo(playerInfo.get()));
        } else {
            TierCache.searchPlayer(searchName).thenAccept(p ->
                    Minecraft.getInstance().execute(() -> {
                        if (p.rankings().isEmpty()) {
                            ctx.getSource().sendError(Component.literal("Player not found."));
                        } else {
                            ctx.getSource().sendFeedback(TierTagger.printPlayerInfo(p));
                        }
                    })
            ).exceptionally(t -> {
                ctx.getSource().sendError(Component.literal("Player not found."));
                return null;
            });
        }

        return 0;
    }
}
