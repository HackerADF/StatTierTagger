package dev.adf.stattier.model;

import java.util.Arrays;
import java.util.Optional;

public enum TierList {
   MCTIERS("MCTiers", "https://mctiers.com/api", '\ue901'),
   SUBTIERS("SubTiers", "https://subtiers.net/api", '\ue902');

   private final String name;
   private final String url;
   private final char icon;

   public String styledName(boolean current) {
      String s = this.icon + " " + this.name;
      if (current) {
         s = s + " (selected)";
      }

      return s;
   }

   public static Optional<TierList> findByUrl(String url) {
      final String normalizedUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

      return Arrays.stream(values()).filter((list) -> {
         return list.url.equals(normalizedUrl);
      }).findFirst();
   }

public String getName() {
      return this.name;
   }

public String getUrl() {
      return this.url;
   }

public char getIcon() {
      return this.icon;
   }

private TierList(final String name, final String url, final char icon) {
      this.name = name;
      this.url = url;
      this.icon = icon;
   }

   private static TierList[] $values() {
      return new TierList[]{MCTIERS, SUBTIERS};
   }
}
