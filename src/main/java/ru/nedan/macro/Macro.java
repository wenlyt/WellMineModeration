package ru.nedan.macro;

import lombok.Getter;
import lombok.Setter;
import ru.nedan.util.Wrapper;

@Getter
public class Macro implements Wrapper {
    private final String name, command;

    @Setter
    private int key;

    @Setter
    private boolean hasBind;

    public Macro(String name, String command) {
        this.name = name;
        this.command = command;
    }

    public void sendMessage() {
        mc.player.sendChatMessage(command);
    }

}
