package ru.nedan.module.api.setting;

import lombok.Getter;
import ru.nedan.util.Configure;

import java.util.function.Supplier;

@Getter
public abstract class Setting implements Configure {
    private final String name;
    private Supplier<Boolean> visible = () -> true;

    public Setting(String name, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public double getHeight() {
        return 16;
    }
}
