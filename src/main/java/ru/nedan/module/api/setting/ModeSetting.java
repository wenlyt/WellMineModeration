package ru.nedan.module.api.setting;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class ModeSetting extends Setting {
    private final List<String> modes;
    private int index;
    private String mode;

    public ModeSetting(String name, String mode, Supplier<Boolean> visible, String... modes) {
        super(name, visible);
        this.modes = Arrays.asList(modes);

        if (!this.modes.contains(mode)) throw new RuntimeException("Сеттинг " + name + " не включает в себя мод " + mode + ". Допустимые значения: " + Arrays.toString(modes));

        this.mode = mode;
        this.index = this.modes.indexOf(mode);
    }

    public boolean is(String mode) {
        return this.mode.equals(mode);
    }

    public void cycle() {
        if (index >= this.modes.size() - 1) {
            mode = modes.get(0);
            index = 0;
        } else {
            index++;
            mode = modes.get(index);
        }
    }

    @Override
    public void addConfig(JsonObject jsonObject) {
        JsonObject thisObject = new JsonObject();
        thisObject.addProperty("mode", this.mode);
        jsonObject.add(this.getName(), thisObject);
    }

    @Override
    public void readFromConfig(JsonObject jsonObject) {
        JsonObject thisObject = jsonObject.get(this.getName()).getAsJsonObject();
        this.mode = thisObject.get("mode").getAsString();
        this.index = this.modes.indexOf(this.mode);
    }
}