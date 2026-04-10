package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.utils.Ukutils;

public class PlayerSearchScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget textField;
    private ButtonWidget searchButton;
    private boolean searching = false;

    public PlayerSearchScreen(Screen parent) {
        super(Text.of("Player Search"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20,
                Text.translatable("stattier.search.user"));
        this.textField.setMaxLength(32);
        this.textField.setTextPredicate(s -> s.matches("[a-zA-Z0-9_-]*"));
        this.addSelectableChild(this.textField);

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
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchButton.active = !this.textField.getText().isEmpty() && !searching;
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
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        this.textField.render(context, mouseX, mouseY, delta);
    }
}
