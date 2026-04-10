package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class PlayerInfoScreen extends CloseableScreen {
    private static final Map<String, Integer> REGION_COLORS = Map.of(
            "NA", 0xff6a6e, "EU", 0x6aff6e, "SA", 0xff9900,
            "AU", 0xf6b26b, "ME", 0xffd966, "AS", 0xc27ba0, "AF", 0x674ea7
    );

    private final PlayerInfo info;
    private final PlayerSkinWidget skin;

    public PlayerInfoScreen(Screen parent, PlayerInfo info, PlayerSkinWidget skin) {
        super(Component.literal("Player Info"), parent);
        this.info = info;
        this.skin = skin;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> Minecraft.getInstance().setScreen(parent))
                .bounds(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());

        this.addRenderableWidget(this.skin);

        int rankingHeight = this.info.rankings().size() * 11;
        int rank = TierCache.getPlayerRank(this.info.name());
        int infoHeight = rank > 0 ? 60 : 45;
        int startY = (this.height - infoHeight - rankingHeight) / 2;
        int rankingY = startY + infoHeight;

        for (PlayerInfo.NamedRanking namedRanking : this.info.getSortedTiers()) {
            if (namedRanking.mode() == null) continue;

            StringWidget text = new StringWidget(formatTier(namedRanking.mode(), namedRanking.ranking()), this.font);
            text.setX(this.width / 2 + 5);
            text.setY(rankingY);
            this.addRenderableWidget(text);
            rankingY += 11;
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredString(this.font, this.info.name() + "'s profile", this.width / 2, 20, 0xFFFFFFFF);

        int rankingHeight = this.info.rankings().size() * 11;
        int rank = TierCache.getPlayerRank(this.info.name());
        int infoHeight = rank > 0 ? 60 : 45;
        int startY = (this.height - infoHeight - rankingHeight) / 2;

        context.drawString(this.font, getRegionText(this.info), this.width / 2 + 5, startY, 0xFFFFFFFF);
        context.drawString(this.font, getTitleText(this.info), this.width / 2 + 5, startY + 15, 0xFFFFFFFF);

        if (rank > 0) {
            context.drawString(this.font, getRankText(rank), this.width / 2 + 5, startY + 30, 0xFFFFFFFF);
            context.drawString(this.font, "Rankings:", this.width / 2 + 5, startY + 45, 0xFFFFFFFF);
        } else {
            context.drawString(this.font, "Rankings:", this.width / 2 + 5, startY + 30, 0xFFFFFFFF);
        }
    }

    private Component formatTier(@NotNull GameMode gamemode, PlayerInfo.Ranking ranking) {
        Component tierText = TierTagger.getRankingText(ranking, false);
        return Component.empty()
                .append(gamemode.asStyled(true))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(tierText);
    }

    private Component getTitleText(PlayerInfo info) {
        PlayerInfo.PointInfo pointInfo = info.getPointInfo();
        return Component.empty()
                .append(Component.literal(pointInfo.getTitle()).withStyle(s -> s.withColor(pointInfo.getColor())))
                .append(Component.literal(" (" + info.getTotalPoints() + " pts)").withStyle(ChatFormatting.GRAY));
    }

    private Component getRankText(int rank) {
        int rankColor = TierTagger.getRankColor(rank);
        return Component.empty()
                .append(Component.literal("Rank: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("#" + rank).withStyle(s -> s.withColor(rankColor)));
    }

    private Component getRegionText(PlayerInfo info) {
        int color = REGION_COLORS.getOrDefault(info.region().toUpperCase(Locale.ROOT), 0xffffff);
        return Component.empty()
                .append(Component.literal("Region: "))
                .append(Component.literal(info.region()).withStyle(s -> s.withColor(color)));
    }
}
