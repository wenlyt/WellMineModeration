package ru.nedan.event.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nedan.event.api.Event;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class EventMotion extends Event {
    private float yaw, pitch;
}
