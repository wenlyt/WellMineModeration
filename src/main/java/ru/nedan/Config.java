package ru.nedan;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.nedan.module.api.Module;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static final String CONFIG_PATH = "./config/";

    public static void saveConfig(String name) {
        File file = new File(CONFIG_PATH);
        file.mkdirs();

        try (FileWriter writer = new FileWriter(CONFIG_PATH + name + ".json")) {
            JsonObject main = new JsonObject();
            JsonObject modules = new JsonObject();

            for (Module module : Quantumclient.getInstance().moduleStorage.getModules()) {
                module.addConfig(modules);
            }

            main.add("modules", modules);
            writer.write(main.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readConfig(String name) {
        try (FileReader reader = new FileReader(CONFIG_PATH + name + ".json")) {
            JsonObject object = new JsonParser().parse(reader).getAsJsonObject();

            JsonObject modules = object.get("modules").getAsJsonObject();

            for (Module module : Quantumclient.getInstance().moduleStorage.getModules())
                module.readFromConfig(modules);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
