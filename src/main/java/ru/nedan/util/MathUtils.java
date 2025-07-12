package ru.nedan.util;

import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@UtilityClass
public class MathUtils implements Wrapper {

    public static double deltaTime() {
        return getFps() > 0 ? (1.0000 / getFps()) : 1;
    }

    public int getFps() {
        try {
            for (Field field : mc.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                    field.setAccessible(true);
                    return field.getInt(null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return 60;
    }

    public static float lerp(float end, float start, float multiple) {
        return (float) (end + (start - end) * MathHelper.clamp(deltaTime() * multiple, 0, 1));
    }

    public Vec2f getMousePos() {
        float i = (float) (mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
        float j = (float) (mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());

        return new Vec2f(i, j);
    }

}
