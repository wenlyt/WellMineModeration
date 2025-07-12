package ru.nedan.fonts;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Fonts {
    Cygra("cygra.ttf"),
    Inter("inter-semibold.ttf"),
    Icons("icons.ttf");

    private final String file;
    private final Float2ObjectMap<Font> fontMap = new Float2ObjectArrayMap<>();

    public Font get(float size) {
        return fontMap.computeIfAbsent(size, font -> {
            try {
                return Font.create(getFile(), size, false, false, false);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load font: " + this, e);
            }
        });
    }

}