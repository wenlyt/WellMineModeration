package ru.nedan.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import ru.nedan.event.api.Event;

@Getter
@AllArgsConstructor
public class EventRender2D extends Event {
    private MatrixStack matrixStack;
    private float partialTicks;
}
