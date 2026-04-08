package dev.adf.stattier.tierlist;

import dev.adf.stattier.TierCache;
import dev.adf.stattier.mixin.MinecraftClientAccessor;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7497;
import net.minecraft.class_8685;
import net.minecraft.class_8765;
import net.uku3lig.ukulib.config.option.widget.TextInputWidget;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import net.uku3lig.ukulib.utils.Ukutils;

public class PlayerSearchScreen extends CloseableScreen {
   private TextInputWidget textField;
   private class_4185 searchButton;
   private boolean searching = false;

   public PlayerSearchScreen(class_437 parent) {
      super("Player Search", parent);
   }

   protected void method_25426() {
      String username = class_1074.method_4662("stattier.search.user", new Object[0]);
      this.textField = (TextInputWidget)this.method_25429(new TextInputWidget(this.field_22789 / 2 - 100, 116, 200, 20, "", (s) -> {
      }, username, (s) -> {
         return s.matches("[a-zA-Z0-9_-]+");
      }, 32));
      this.searchButton = (class_4185)this.method_37063(class_4185.method_46430(class_2561.method_43471("stattier.search"), (button) -> {
         this.loadAndShowProfile();
      }).method_46434(this.field_22789 / 2 - 100, this.field_22790 / 4 + 96 + 12, 200, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_5244.field_24335, (button) -> {
         this.method_25419();
      }).method_46434(this.field_22789 / 2 - 100, this.field_22790 / 4 + 120 + 12, 200, 20).method_46431());
      this.method_48265(this.textField);
   }

   public void method_25393() {
      super.method_25393();
      this.searchButton.field_22763 = this.textField.isValid() && !this.searching;
   }

   private void loadAndShowProfile() {
      String username = this.textField.getText();
      this.searching = true;
      this.searchButton.method_25355(class_2561.method_43471("stattier.search.loading"));

      YggdrasilAuthenticationService service = ((MinecraftClientAccessor)class_310.method_1551()).getAuthenticationService();
      class_7497 services = class_7497.method_44143(service, class_310.method_1551().field_1697);
      CompletableFuture<class_8765> skinFuture = this.fetchProfile(username, services).thenApply((p) -> {
         GameProfile profile = (GameProfile)Optional.ofNullable(services.comp_837().fetchProfile(p.getId(), true)).map(ProfileResult::profile).orElseGet(() -> {
            return new GameProfile(UUID.randomUUID(), username);
         });
         Supplier<class_8685> skinSupplier = class_310.method_1551().method_1582().method_52858(profile);
         class_8765 skin = new class_8765(60, 144, class_310.method_1551().method_31974(), skinSupplier);
         skin.method_48229(this.field_22789 / 2 - 65, (this.field_22790 - 144) / 2);
         return skin;
      }).exceptionally((t) -> {
         GameProfile profile = new GameProfile(UUID.randomUUID(), username);
         Supplier<class_8685> skinSupplier = class_310.method_1551().method_1582().method_52858(profile);
         class_8765 skin = new class_8765(60, 144, class_310.method_1551().method_31974(), skinSupplier);
         skin.method_48229(this.field_22789 / 2 - 65, (this.field_22790 - 144) / 2);
         return skin;
      });

      TierCache.searchPlayer(username).thenCombine(skinFuture, (info, skin) -> {
         return new PlayerInfoScreen(this, info, skin);
      }).thenAccept((screen) -> {
         class_310.method_1551().execute(() -> {
            class_310.method_1551().method_1507(screen);
         });
      }).whenComplete((v, t) -> {
         if (t != null) {
            Ukutils.sendToast(class_2561.method_43471("stattier.search.unknown"), (class_2561)null);
         }

         this.searching = false;
         this.searchButton.method_25355(class_2561.method_43471("stattier.search"));
      });
   }

   private CompletableFuture<GameProfile> fetchProfile(String username, class_7497 services) {
      final CompletableFuture<GameProfile> future = new CompletableFuture();
      services.comp_839().findProfilesByNames(new String[]{username}, new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile profile) {
            future.complete(profile);
         }

         public void onProfileLookupFailed(String profileName, Exception exception) {
            future.completeExceptionally(exception);
         }
      });
      return future;
   }

   public void method_25410(class_310 client, int width, int height) {
      String string = this.textField.getText();
      this.method_25423(client, width, height);
      this.textField.setText(string);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, 16777215);
      this.textField.method_25394(context, mouseX, mouseY, delta);
   }
}
