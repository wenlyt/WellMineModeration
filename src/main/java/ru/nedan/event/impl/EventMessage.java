package ru.nedan.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nedan.event.api.Event;

@Getter
@AllArgsConstructor
public class EventMessage extends Event {
    private String text;
    private boolean send;
}
