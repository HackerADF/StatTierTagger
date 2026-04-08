package dev.adf.stattier.config;

import java.util.function.UnaryOperator;
import net.minecraft.class_437;
import net.uku3lig.ukulib.api.UkulibAPI;

public class UkulibIntegration implements UkulibAPI {
   public UnaryOperator<class_437> supplyConfigScreen() {
      return TTConfigScreen::new;
   }
}
