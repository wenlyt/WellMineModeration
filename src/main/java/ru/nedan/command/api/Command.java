package ru.nedan.command.api;

import lombok.Getter;
import ru.nedan.util.Wrapper;

@Getter
public abstract class Command implements Wrapper {
    private final String name, usage;

    public Command(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }

    public abstract void execute(String... args);
}
