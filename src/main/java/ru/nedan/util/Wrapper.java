package ru.nedan.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

public interface Wrapper {
    MinecraftClient mc = MinecraftClient.getInstance();
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
}
