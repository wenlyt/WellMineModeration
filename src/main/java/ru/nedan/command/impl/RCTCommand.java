package ru.nedan.command.impl;

import ru.nedan.Quantumclient;
import ru.nedan.command.api.Command;
import ru.nedan.util.ChatUtil;

public class RCTCommand extends Command {

    private Thread reconnectThread;

    public RCTCommand() {
        super("rct", ".rct");
    }

    @Override
    public void execute(String... args) {
        try {
            String anarchy = Quantumclient.getInstance().getAnarchy();

            if (anarchy.equalsIgnoreCase("none")) {
                ChatUtil.addMessage("Вы не находитесь на анархии!");
                return;
            }

            if (reconnectThread == null) {
                reconnectThread = new Thread(() -> {
                    mc.player.sendChatMessage("/hub");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    mc.player.sendChatMessage("/an" + anarchy);

                    reconnectThread.interrupt();
                    reconnectThread = null;
                });

                reconnectThread.start();
            }
        } catch (Exception e) {
            ChatUtil.addMessage(this.getUsage());
        }
    }
}
