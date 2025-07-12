package ru.nedan.command.api;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import ru.nedan.Quantumclient;
import ru.nedan.command.impl.*;
import ru.nedan.event.impl.EventMessage;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandStorage {
    private final List<Command> commands;

    public CommandStorage() {
        commands = new ArrayList<>();
        commands.add(new MacroCommand());

        Quantumclient.getInstance().eventBus.register(this);
    }

    @Subscribe
    public void onMessage(EventMessage e) {
        if (e.isSend()) {
            String message = e.getText();

            if (message.startsWith(".")) {
                e.cancel();

                String messageWithoutDot = message.substring(1);
                String[] args = messageWithoutDot.split(" ");

                for (Command command : commands) {
                    if (command.getName().equalsIgnoreCase(args[0])) {
                        command.execute(args);
                        break;
                    }
                }
            }
        }
    }

}
