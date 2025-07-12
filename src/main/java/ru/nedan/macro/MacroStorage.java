package ru.nedan.macro;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MacroStorage {
    private final List<Macro> macros;

    public MacroStorage() {
        macros = new ArrayList<>();
    }

    public void addMacro(Macro macro, Runnable onAdd) {
        macros.add(macro);
        onAdd.run();
    }

    public void removeMacro(String name, Runnable onRemove) {
        macros.removeIf(macro -> macro.getName().equalsIgnoreCase(name));
        onRemove.run();
    }

    public boolean containsMacro(String name) {
        return macros.stream().anyMatch(macro -> macro.getName().equalsIgnoreCase(name));
    }

}
