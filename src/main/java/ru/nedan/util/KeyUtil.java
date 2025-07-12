package ru.nedan.util;

import lombok.experimental.UtilityClass;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashMap;

@UtilityClass
public class KeyUtil {
    private final HashMap<Integer, String> keyCodes = new HashMap<>();

    static {
        try {
            Field[] fields = GLFW.class.getDeclaredFields();

            for (Field field : fields) {
                if (field.getName().startsWith("GLFW_KEY_")) {
                    keyCodes.put(field.getInt(null), field.getName().replaceAll("GLFW_KEY_", ""));
                }
            }

            keyCodes.put(400, "LMB");
            keyCodes.put(401, "RMB");
            keyCodes.put(402, "MIDDLE");
            keyCodes.put(403, "MOUSE2");
            keyCodes.put(404, "MOUSE3");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static String getKeyName(int key) {
        if (!keyCodes.containsKey(key)) return "none";

        return keyCodes.get(key);
    }
}
