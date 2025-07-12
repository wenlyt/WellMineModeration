package ru.nedan.command.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import ru.nedan.command.api.Command;
import ru.nedan.util.ChatUtil;

import java.lang.reflect.Field;

public class LoginCommand extends Command {

    public LoginCommand() {
        super("login", ".login [username]");
    }

    @Override
    public void execute(String... args) {
        try {
            ChatUtil.addMessage("Успешно установил никнейм: " + args[1]);
            setSession(args[1]);
        } catch (Exception e) {
            ChatUtil.addMessage(this.getUsage());
        }
    }

    private void setSession(String username) {
        Session session = new Session(username, username, "0", "legacy");

        try {
            for (Field field : MinecraftClient.class.getDeclaredFields()) {
                if (field.getType().isInstance(session)) {
                    field.setAccessible(true);
                    field.set(mc, session);
                    field.setAccessible(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
