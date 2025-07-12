package ru.nedan.module.api.setting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
public class BindSetting extends Setting {
    private int key;

    public BindSetting(String name, int key, Supplier<Boolean> visible) {
        super(name, visible);
        this.key = key;
    }

    @Override
    public void addConfig(JsonObject jsonObject) {
        JsonObject thisObject = new JsonObject();
        thisObject.addProperty("key", this.key);
        jsonObject.add(this.getName(), thisObject);
    }

    @Override
    public void readFromConfig(JsonObject jsonObject) {
        JsonObject thisObject = jsonObject.get(this.getName()).getAsJsonObject();
        this.key = thisObject.get("key").getAsInt();
    }
}
