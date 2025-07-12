package ru.nedan.command.impl;

import com.google.common.eventbus.Subscribe;
import ru.nedan.Quantumclient;
import ru.nedan.command.api.Command;
import ru.nedan.event.impl.EventPress;
import ru.nedan.macro.Macro;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.KeyUtil;

import java.util.Arrays;

public class MacroCommand extends Command {

    public MacroCommand() {
        super("macro", ".macro add Name Command | .macro remove Name");

        Quantumclient.getInstance().eventBus.register(this);
    }

    @Override
    public void execute(String... args) {
        try {
            if (args[1].equalsIgnoreCase("add")) {
                String name = args[2];
                String command = String.join("", Arrays.copyOfRange(args, 3, args.length));

                Macro macro = new Macro(name, command);
                Quantumclient.getInstance().macroStorage.addMacro(macro, () -> {
                    ChatUtil.addMessage("Макрос \"" + macro.getName() + "\" успешно добавлен! Нажмите на любую клавишу чтобы забиндить!");
                });
            } else if (args[1].equalsIgnoreCase("remove")) {
                String name = args[2];

                if (!Quantumclient.getInstance().macroStorage.containsMacro(name)) {
                    ChatUtil.addMessage("Нету макроса с названием \"" + name + "\"");
                    return;
                }

                Quantumclient.getInstance().macroStorage.removeMacro(name, () -> {
                    ChatUtil.addMessage("Макрос с названием \"" + name + "\" успешно удалён!");
                });
            }
        } catch (Exception e) {
            ChatUtil.addMessage(this.getUsage());
        }
    }

    @Subscribe
    public void onKey(EventPress e) {
        if (e.getAction() == 1 && mc.currentScreen == null) {
            for (Macro macro : Quantumclient.getInstance().macroStorage.getMacros()) {
                if (!macro.isHasBind()) {
                    macro.setKey(e.getKey());
                    macro.setHasBind(true);
                    ChatUtil.addMessage("Макрос \"" + macro.getName() + "\" успешно забинен на клавишу " + KeyUtil.getKeyName(e.getKey()));
                } else {
                    if (e.getKey() == macro.getKey())
                        macro.sendMessage();
                }
            }
        }
    }
}
