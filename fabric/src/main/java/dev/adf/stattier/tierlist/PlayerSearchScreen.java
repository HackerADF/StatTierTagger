package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.mixin.MinecraftClientAccessor;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Services;
import net.uku3lig.ukulib.config.option.widget.TextInputWidget;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import net.uku3lig.ukulib.utils.Ukutils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PlayerSearchScreen extends CloseableScreen {
    private TextInputWidget textField;
    private Button searchButton;
    private boolean searching = false;

    public PlayerSearchScreen(Screen parent) {
        super("Player Search", parent);
    }

    @Override
    protected void init() {
        String username = I18n.get("stattier.search.user");
        this.textField = this.addWidget(new TextInputWidget(this.width / 2 - 100, 116, 200, 20,
                "", s -> {}, username, s -> s.matches("[a-zA-Z0-9_-]+"), 32));

        this.searchButton = this.addRenderableWidget(
                Button.builder(Component.translatable("stattier.search"), button -> this.loadAndShowProfile())
                        .bounds(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20)
                        .build());

        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                        .bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20)
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
        this.searchButton.setMessage(Component.translatable("stattier.search.loading"));

        YggdrasilAuthenticationService service = ((MinecraftClientAccessor) Minecraft.getInstance()).getAuthenticationService();
        Services services = Services.create(service, Minecraft.getInstance().gameDirectory);

        CompletableFuture<PlayerSkinWidget> skinFuture = fetchProfile(username, services).thenApply(p -> {
            GameProfile profile = Optional.ofNullable(services.sessionService().fetchProfile(p.getId(), true))
                    .map(ProfileResult::profile)
                    .orElseGet(() -> new GameProfile(UUID.randomUUID(), username));

            Supplier<PlayerSkin> skinSupplier = Minecraft.getInstance().getSkinManager().lookupInsecure(profile);
            PlayerSkinWidget skin = new PlayerSkinWidget(60, 144, Minecraft.getInstance().getEntityModels(), skinSupplier);
            skin.setPosition(this.width / 2 - 65, (this.height - 144) / 2);
            return skin;
        }).exceptionally(t -> {
            GameProfile profile = new GameProfile(UUID.randomUUID(), username);
            Supplier<PlayerSkin> skinSupplier = Minecraft.getInstance().getSkinManager().lookupInsecure(profile);
            PlayerSkinWidget skin = new PlayerSkinWidget(60, 144, Minecraft.getInstance().getEntityModels(), skinSupplier);
            skin.setPosition(this.width / 2 - 65, (this.height - 144) / 2);
            return skin;
        });

        TierCache.searchPlayer(username)
                .thenCombine(skinFuture, (info, skin) -> new PlayerInfoScreen(this, info, skin))
                .thenAccept(screen -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(screen)))
                .whenComplete((v, t) -> {
                    if (t != null) {
                        Ukutils.sendToast(Component.translatable("stattier.search.unknown"), null);
                    }
                    this.searching = false;
                    this.searchButton.setMessage(Component.translatable("stattier.search"));
                });
    }

    private CompletableFuture<GameProfile> fetchProfile(String username, Services services) {
        CompletableFuture<GameProfile> future = new CompletableFuture<>();

        services.profileRepository().findProfilesByNames(new String[]{username}, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                future.complete(profile);
            }

            @Override
            public void onProfileLookupFailed(String profileName, Exception exception) {
                future.completeExceptionally(exception);
            }
        });

        return future;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.textField.setText(string);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        this.textField.render(context, mouseX, mouseY, delta);
    }
}
