package ru.nedan.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.Packet;
import ru.nedan.event.api.Event;

@Getter
@AllArgsConstructor
public class EventPacket extends Event {
    private Packet<?> packet;
    private boolean send;
}
