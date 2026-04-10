package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.option.widget.TextInputWidget;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import net.uku3lig.ukulib.utils.Ukutils;

public class PlayerSearchScreen extends CloseableScreen {
    private TextInputWidget textField;
    private ButtonWidget searchButton;
    private boolean searching = false;

    public PlayerSearchScreen(Screen parent) {
        super("Player Search", parent);
    }

    @Override
    protected void init() {
        String username = I18n.translate("stattier.search.user");
        this.textField = this.addSelectableChild(new TextInputWidget(this.width / 2 - 100, 116, 200, 20,
                "", s -> {}, username, s -> s.matches("[a-zA-Z0-9_-]+"), 32));

        this.searchButton = this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("stattier.search"), button -> this.loadAndShowProfile())
                        .dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20)
                        .build());

        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
                        .dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20)
                        .build());

        this.setInitialFocus(this.textField);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchButton.active = this.textField.isValid() && !searching;
    }

    private void loadAndShowProfile() {
        String username = this.textField.getText();
        this.searching = true;
        this.searchButton.setMessage(Text.translatable("stattier.search.loading"));

        TierCache.searchPlayer(username)
                .thenAccept(info -> MinecraftClient.getInstance().execute(() ->
                        MinecraftClient.getInstance().setScreen(new PlayerInfoScreen(this, info))))
                .whenComplete((v, t) -> {
                    if (t != null) {
                        Ukutils.sendToast(Text.translatable("stattier.search.unknown"), null);
                    }
                    this.searching = false;
                    this.searchButton.setMessage(Text.translatable("stattier.search"));
                });
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.textField.setText(string);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        this.textField.render(context, mouseX, mouseY, delta);
    }
}
