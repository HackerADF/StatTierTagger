package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.TierTagger;
import dev.adf.stattier.model.GameMode;
import dev.adf.stattier.model.PlayerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PlayerSkinWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
        super(Text.of("Player Info"), parent);
        this.info = info;
        this.skin = skin;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> MinecraftClient.getInstance().setScreen(parent))
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());

        this.addDrawableChild(this.skin);

        int rankingHeight = this.info.rankings().size() * 11;
        int rank = TierCache.getPlayerRank(this.info.name());
        int infoHeight = rank > 0 ? 60 : 45;
        int startY = (this.height - infoHeight - rankingHeight) / 2;
        int rankingY = startY + infoHeight;

        for (PlayerInfo.NamedRanking namedRanking : this.info.getSortedTiers()) {
            if (namedRanking.mode() == null) continue;

            TextWidget text = new TextWidget(formatTier(namedRanking.mode(), namedRanking.ranking()), this.textRenderer);
            text.setX(this.width / 2 + 5);
            text.setY(rankingY);
            this.addDrawableChild(text);
            rankingY += 11;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.info.name() + "'s profile", this.width / 2, 20, 0xFFFFFFFF);

        int rankingHeight = this.info.rankings().size() * 11;
        int rank = TierCache.getPlayerRank(this.info.name());
        int infoHeight = rank > 0 ? 60 : 45;
        int startY = (this.height - infoHeight - rankingHeight) / 2;

        context.drawTextWithShadow(this.textRenderer, getRegionText(this.info), this.width / 2 + 5, startY, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, getTitleText(this.info), this.width / 2 + 5, startY + 15, 0xFFFFFFFF);

        if (rank > 0) {
            context.drawTextWithShadow(this.textRenderer, getRankText(rank), this.width / 2 + 5, startY + 30, 0xFFFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "Rankings:", this.width / 2 + 5, startY + 45, 0xFFFFFFFF);
        } else {
            context.drawTextWithShadow(this.textRenderer, "Rankings:", this.width / 2 + 5, startY + 30, 0xFFFFFFFF);
        }
    }

    private Text formatTier(@NotNull GameMode gamemode, PlayerInfo.Ranking ranking) {
        Text tierText = TierTagger.getRankingText(ranking, false);
        return Text.empty()
                .append(gamemode.asStyled(true))
                .append(Text.literal(": ").formatted(Formatting.GRAY))
                .append(tierText);
    }

    private Text getTitleText(PlayerInfo info) {
        PlayerInfo.PointInfo pointInfo = info.getPointInfo();
        return Text.empty()
                .append(Text.literal(pointInfo.getTitle()).styled(s -> s.withColor(pointInfo.getColor())))
                .append(Text.literal(" (" + info.getTotalPoints() + " pts)").formatted(Formatting.GRAY));
    }

    private Text getRankText(int rank) {
        int rankColor = TierTagger.getRankColor(rank);
        return Text.empty()
                .append(Text.literal("Rank: ").formatted(Formatting.GRAY))
                .append(Text.literal("#" + rank).styled(s -> s.withColor(rankColor)));
    }

    private Text getRegionText(PlayerInfo info) {
        int color = REGION_COLORS.getOrDefault(info.region().toUpperCase(Locale.ROOT), 0xffffff);
        return Text.empty()
                .append(Text.literal("Region: "))
                .append(Text.literal(info.region()).styled(s -> s.withColor(color)));
    }
}
