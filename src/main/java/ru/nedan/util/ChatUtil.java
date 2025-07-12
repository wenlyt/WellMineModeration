package ru.nedan.util;

import lombok.experimental.UtilityClass;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

@UtilityClass
public class ChatUtil implements Wrapper {

    public void addMessage(Object message) {
        if (mc.inGameHud != null) {
            MutableText literalText = new LiteralText("QuantumClient").formatted(Formatting.AQUA).append(new LiteralText(" -> ").formatted(Formatting.RED)).append(new LiteralText(String.valueOf(message)).formatted(Formatting.WHITE));

            mc.inGameHud.getChatHud().addMessage(literalText);
        } else {
            System.out.println(message);
        }
    }

}
