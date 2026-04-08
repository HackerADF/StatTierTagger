package dev.adf.stattier.model;

import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_3545;
import net.minecraft.class_5251;

public record GameMode(String id, String title) {
   public static final GameMode NONE = new GameMode("annoying_long_id_that_no_one_will_ever_use_just_to_make_sure", "§cNone§r");

   public GameMode(String id, String title) {
      this.id = id;
      this.title = title;
   }

   public boolean isNone() {
      return this.id.equals(NONE.id);
   }

   private class_3545<Character, class_5251> iconAndColor() {
      String var1 = this.id;
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1363067405:
         if (var1.equals("minecart")) {
            var2 = 17;
         }
         break;
      case -1335741356:
         if (var1.equals("debuff")) {
            var2 = 12;
         }
         break;
      case -1299962959:
         if (var1.equals("elytra")) {
            var2 = 15;
         }
         break;
      case -1059982798:
         if (var1.equals("trident")) {
            var2 = 20;
         }
         break;
      case -1048925812:
         if (var1.equals("nethop")) {
            var2 = 2;
         }
         break;
      case 97038:
         if (var1.equals("axe")) {
            var2 = 0;
         }
         break;
      case 97409:
         if (var1.equals("bed")) {
            var2 = 9;
         }
         break;
      case 97738:
         if (var1.equals("bow")) {
            var2 = 10;
         }
         break;
      case 111189:
         if (var1.equals("pot")) {
            var2 = 4;
         }
         break;
      case 114006:
         if (var1.equals("smp")) {
            var2 = 5;
         }
         break;
      case 115760:
         if (var1.equals("uhc")) {
            var2 = 7;
         }
         break;
      case 3343606:
         if (var1.equals("mace")) {
            var2 = 1;
         }
         break;
      case 109641799:
         if (var1.equals("speed")) {
            var2 = 19;
         }
         break;
      case 109860349:
         if (var1.equals("sword")) {
            var2 = 6;
         }
         break;
      case 233102203:
         if (var1.equals("vanilla")) {
            var2 = 8;
         }
         break;
      case 768948244:
         if (var1.equals("og_vanilla")) {
            var2 = 18;
         }
         break;
      case 835482605:
         if (var1.equals("manhunt")) {
            var2 = 16;
         }
         break;
      case 1028669806:
         if (var1.equals("creeper")) {
            var2 = 11;
         }
         break;
      case 1299136129:
         if (var1.equals("neth_pot")) {
            var2 = 3;
         }
         break;
      case 1415803507:
         if (var1.equals("dia_crystal")) {
            var2 = 13;
         }
         break;
      case 1654641427:
         if (var1.equals("dia_smp")) {
            var2 = 14;
         }
         break;
      case 1047561014:
         if (var1.equals("crystal")) {
            var2 = 21;
         }
         break;
      case -1332081575:
         if (var1.equals("diapot")) {
            var2 = 22;
         }
         break;
      case 1843039242:
         if (var1.equals("nethpot")) {
            var2 = 23;
         }
         break;
      }

      class_3545 var10000;
      switch(var2) {
      case 0:
         var10000 = new class_3545('\ue701', class_5251.method_27718(class_124.field_1060));
         break;
      case 1:
         var10000 = new class_3545('\ue702', class_5251.method_27718(class_124.field_1080));
         break;
      case 2:
      case 3:
         var10000 = new class_3545('\ue703', class_5251.method_27717(8211008));
         break;
      case 4:
         var10000 = new class_3545('\ue704', class_5251.method_27717(16711680));
         break;
      case 5:
         var10000 = new class_3545('\ue705', class_5251.method_27717(15518533));
         break;
      case 6:
         var10000 = new class_3545('\ue706', class_5251.method_27717(10812912));
         break;
      case 7:
         var10000 = new class_3545('\ue707', class_5251.method_27718(class_124.field_1061));
         break;
      case 8:
         var10000 = new class_3545('\ue708', class_5251.method_27718(class_124.field_1076));
         break;
      case 9:
         var10000 = new class_3545('\ue801', class_5251.method_27717(16711680));
         break;
      case 10:
         var10000 = new class_3545('\ue802', class_5251.method_27717(6700304));
         break;
      case 11:
         var10000 = new class_3545('\ue803', class_5251.method_27718(class_124.field_1060));
         break;
      case 12:
         var10000 = new class_3545('\ue804', class_5251.method_27718(class_124.field_1063));
         break;
      case 13:
         var10000 = new class_3545('\ue805', class_5251.method_27718(class_124.field_1075));
         break;
      case 14:
         var10000 = new class_3545('\ue806', class_5251.method_27717(9201291));
         break;
      case 15:
         var10000 = new class_3545('\ue807', class_5251.method_27717(9276849));
         break;
      case 16:
         var10000 = new class_3545('\ue808', class_5251.method_27718(class_124.field_1061));
         break;
      case 17:
         var10000 = new class_3545('\ue809', class_5251.method_27718(class_124.field_1080));
         break;
      case 18:
         var10000 = new class_3545('\ue810', class_5251.method_27718(class_124.field_1065));
         break;
      case 19:
         var10000 = new class_3545('\ue811', class_5251.method_27717(4434385));
         break;
      case 20:
         var10000 = new class_3545('\ue812', class_5251.method_27717(5741452));
         break;
      case 21:
         var10000 = new class_3545('\ue805', class_5251.method_27718(class_124.field_1075));
         break;
      case 22:
         var10000 = new class_3545('\ue704', class_5251.method_27717(16711680));
         break;
      case 23:
         var10000 = new class_3545('\ue703', class_5251.method_27717(8211008));
         break;
      default:
         var10000 = new class_3545('•', class_5251.method_27718(class_124.field_1068));
      }

      return var10000;
   }

   public Optional<Character> icon() {
      class_3545<Character, class_5251> pair = this.iconAndColor();
      return ((class_5251)pair.method_15441()).method_27716() == 16777215 ? Optional.empty() : Optional.of((Character)pair.method_15442());
   }

   public class_2561 asStyled(boolean withDefaultDot) {
      class_3545<Character, class_5251> pair = this.iconAndColor();
      if (((class_5251)pair.method_15441()).method_27716() == 16777215 && !withDefaultDot) {
         return class_2561.method_30163(this.title);
      } else {
         class_2561 name = class_2561.method_43470(this.title).method_27694((s) -> {
            return s.method_27703((class_5251)pair.method_15441());
         });
         return class_2561.method_43470(String.valueOf(pair.method_15442()) + " ").method_10852(name);
      }
   }

   public String id() {
      return this.id;
   }

   public String title() {
      return this.title;
   }
}
