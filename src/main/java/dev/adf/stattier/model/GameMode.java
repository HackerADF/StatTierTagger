package dev.adf.stattier.model;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.Optional;

public record GameMode(String id, String title) {
    public static final GameMode NONE = new GameMode("annoying_long_id_that_no_one_will_ever_use_just_to_make_sure", "\u00a7cNone\u00a7r");

    public boolean isNone() {
        return this.id.equals(NONE.id);
    }

    private record IconAndColor(char icon, TextColor color) {}

    private IconAndColor iconAndColor() {
        return switch (this.id) {
            case "axe" -> new IconAndColor('\uE701', TextColor.fromLegacyFormat(ChatFormatting.GREEN));
            case "mace" -> new IconAndColor('\uE702', TextColor.fromLegacyFormat(ChatFormatting.GRAY));
            case "nethop", "neth_pot", "nethpot" -> new IconAndColor('\uE703', TextColor.fromRgb(0x7d4a40));
            case "pot", "diapot" -> new IconAndColor('\uE704', TextColor.fromRgb(0xff0000));
            case "smp" -> new IconAndColor('\uE705', TextColor.fromRgb(0xeccb45));
            case "sword" -> new IconAndColor('\uE706', TextColor.fromRgb(0xa4fdf0));
            case "uhc" -> new IconAndColor('\uE707', TextColor.fromLegacyFormat(ChatFormatting.RED));
            case "vanilla" -> new IconAndColor('\uE708', TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE));
            case "bed" -> new IconAndColor('\uE801', TextColor.fromRgb(0xff0000));
            case "bow" -> new IconAndColor('\uE802', TextColor.fromRgb(0x663d10));
            case "creeper" -> new IconAndColor('\uE803', TextColor.fromLegacyFormat(ChatFormatting.GREEN));
            case "debuff" -> new IconAndColor('\uE804', TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY));
            case "dia_crystal", "crystal" -> new IconAndColor('\uE805', TextColor.fromLegacyFormat(ChatFormatting.AQUA));
            case "dia_smp" -> new IconAndColor('\uE806', TextColor.fromRgb(0x8c668b));
            case "elytra" -> new IconAndColor('\uE807', TextColor.fromRgb(0x8d8db1));
            case "manhunt" -> new IconAndColor('\uE808', TextColor.fromLegacyFormat(ChatFormatting.RED));
            case "minecart" -> new IconAndColor('\uE809', TextColor.fromLegacyFormat(ChatFormatting.GRAY));
            case "og_vanilla" -> new IconAndColor('\uE810', TextColor.fromLegacyFormat(ChatFormatting.GOLD));
            case "speed" -> new IconAndColor('\uE811', TextColor.fromRgb(0x43a9d1));
            case "trident" -> new IconAndColor('\uE812', TextColor.fromRgb(0x579b8c));
            default -> new IconAndColor('\u2022', TextColor.fromLegacyFormat(ChatFormatting.WHITE));
        };
    }

    public Optional<Character> icon() {
        IconAndColor pair = this.iconAndColor();
        return pair.color().getValue() == 0xFFFFFF ? Optional.empty() : Optional.of(pair.icon());
    }

    public Component asStyled(boolean withDefaultDot) {
        IconAndColor pair = this.iconAndColor();

        if (pair.color().getValue() == 0xFFFFFF && !withDefaultDot) {
            return Component.literal(this.title);
        } else {
            Component name = Component.literal(this.title).withStyle(s -> s.withColor(pair.color()));
            return Component.literal(pair.icon() + " ").append(name);
        }
    }
}
