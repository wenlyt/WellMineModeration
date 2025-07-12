package ru.nedan.event.impl;

import net.minecraft.client.util.math.MatrixStack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDisplay {
    private MatrixStack matrixStack;
    private float partialTicks;
    private Type type;

    public EventDisplay(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }

    public enum Type {
        PRE, POST, HIGH
    }
}