package ru.nedan.event.api;

import lombok.Getter;
import ru.nedan.Quantumclient;

@Getter
public class Event {
    private boolean canceled;

    public void cancel() {
        canceled = true;
    }

    public void call() {
        if (Quantumclient.PANIC) return;

        Quantumclient.getInstance().eventBus.post(this);
    }

}
