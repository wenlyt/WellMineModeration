package ru.nedan.event.impl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSendMessage {
    private String message;
    private boolean cancelled;

    public EventSendMessage(String message) {
        this.message = message;
        this.cancelled = false;
    }
}