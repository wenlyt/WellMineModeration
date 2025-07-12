package ru.nedan.module.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import ru.nedan.Quantumclient;
import ru.nedan.module.api.setting.Setting;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.Configure;
import ru.nedan.util.Wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Module implements Wrapper, Configure {
    private final String name, desc;

    @Setter
    private int key;

    @Setter
    private boolean enabled;

    @Getter
    private final List<Setting> settings = new ArrayList<>();

    public Module() {
        ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);

        if (moduleInfo == null) {
            throw new RuntimeException("ModuleInfo = null in " + this.getClass().getName());
        }

        this.name = moduleInfo.name();
        this.desc = moduleInfo.desc();
    }

    public void onEnable() {
        Quantumclient.getInstance().eventBus.register(this);
    }

    public void onDisable() {
        Quantumclient.getInstance().eventBus.unregister(this);
    }

    public void toggle() {
        setEnabled(!isEnabled());

        if (isEnabled())
            onEnable();
        else
            onDisable();
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    @Override
    public void addConfig(JsonObject jsonObject) {
        JsonObject thisObject = new JsonObject();

        thisObject.addProperty("key", this.key);
        thisObject.addProperty("enabled", this.enabled);

        JsonObject settings = new JsonObject();

        for (Setting setting : this.settings) {
            setting.addConfig(settings);
        }

        thisObject.add("settings", settings);
        jsonObject.add(this.getName(), thisObject);
    }

    @Override
    public void readFromConfig(JsonObject jsonObject) {
        JsonElement element = jsonObject.get(this.name);

        if (element.isJsonNull()) return;

        JsonObject thisObject = element.getAsJsonObject();

        this.key = thisObject.get("key").getAsInt();
        this.enabled = thisObject.get("enabled").getAsBoolean();

        if (enabled) {
            Quantumclient.getInstance().eventBus.register(this);
        }

        JsonObject settings = thisObject.get("settings").getAsJsonObject();

        for (Setting setting : this.settings)
            setting.readFromConfig(settings);
    }
}
