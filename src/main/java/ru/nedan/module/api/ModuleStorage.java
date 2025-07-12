package ru.nedan.module.api;

import lombok.Getter;
import ru.nedan.module.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ModuleStorage {
    private final List<Module> modules;

    public ModuleStorage() {
        modules = new ArrayList<>();

        modules.addAll(Arrays.asList(
                new ClickGUI(),
                new TargetESP(),
                new HUD()
        ));
    }

    @SuppressWarnings("unchecked")
    public <T> T getModule(Class<T> clazz) {
        for (Module module : modules)
            if (module.getClass() == clazz)
                return (T) module;

        return null;
    }
}
