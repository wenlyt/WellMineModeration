package ru.nedan;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import ru.nedan.command.api.CommandStorage;
import ru.nedan.event.impl.EventPress;
import ru.nedan.macro.MacroStorage;
import ru.nedan.module.api.Module;
import ru.nedan.module.api.ModuleStorage;
import ru.nedan.util.Wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("all")
public class Quantumclient implements ModInitializer, Wrapper {
    @Getter
    private static Quantumclient instance;
    public EventBus eventBus;
    public ModuleStorage moduleStorage;
    public CommandStorage commandStorage;
    public MacroStorage macroStorage;

    public static boolean PANIC;

    @Override
    public void onInitialize() {
    }

    public Quantumclient() {
        long currentTime = System.currentTimeMillis();
        instance = this;

        if (!checkAuth()) {
            System.exit(0);
        }

        eventBus = new EventBus();
        moduleStorage = new ModuleStorage();
        commandStorage = new CommandStorage();
        macroStorage = new MacroStorage();

        eventBus.register(this);

        long lastTime = System.currentTimeMillis() - currentTime;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Config.saveConfig("config");
        }));

        Config.readConfig("config");

        System.out.println("Client loaded in " + lastTime + " milliseconds");
    }

    private boolean checkAuth() {
        try {
            String playerName = mc.getSession().getUsername();

            URL url = new URL("httpgit commit -m \"first commit\"git commit -m \"first commit\"s://pastebin.com/raw/WeFeLjDW");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(playerName)) {
                    reader.close();
                    return true;
                }
            }

            reader.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Subscribe
    public void onKey(EventPress e) {
        if (e.getAction() == 1 && mc.currentScreen == null) {
            for (Module module : moduleStorage.getModules()) {
                if (e.getKey() == module.getKey())
                    module.toggle();
            }
        }
    }

    public String getAnarchy() {
        String anarchy = "none";

        try {
            PlayerListHud playerListHud = mc.inGameHud.getPlayerListHud();

            Field field = PlayerListHud.class.getDeclaredFields()[4];

            field.setAccessible(true);

            Text text = (Text) field.get(playerListHud);
            String str = text.getString();

            String[] split = str.split("\n");

            for (String s : split) {
                if (s.startsWith("Режим: Анархия-")) {
                    anarchy = s.replaceAll("Режим: Анархия-", "");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return anarchy;
    }
}