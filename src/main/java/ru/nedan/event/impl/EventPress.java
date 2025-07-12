package ru.nedan.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nedan.event.api.Event;

@Getter
@AllArgsConstructor
public class EventPress extends Event {
    private int key, action;
}
