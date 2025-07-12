package ru.nedan.module.impl;

import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import ru.nedan.Quantumclient;
import ru.nedan.event.impl.EventRender2D;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.module.api.Module;
import ru.nedan.module.api.ModuleInfo;
import ru.nedan.util.MathUtils;
import ru.nedan.util.shader.Rounds;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "HUD", desc = "2Д визуализация клиента")
public class HUD extends Module {

    public HUD() {
        toggle();
    }

    @Subscribe
    public void onRender2D(EventRender2D e) {
        Window window = mc.getWindow();

        { // WATERMARK
            int x = 10;
            int y = 10;

            String text = "HarmProduct | FPS: " + MathUtils.getFps();

            Font f = Fonts.Cygra.get(18);

            Rounds.drawRound(x, y, f.getWidth(text) + 10, f.getHeight() + 5, 5, Color.BLACK);

            f.draw(e.getMatrixStack(), text, x + 3, y + 3, -1);
        }

        { // ARRAY LIST
            Font font = Fonts.Cygra.get(18);

            List<Module> modules = getSorted(font);

            float offset = 5;

            float x = window.getScaledWidth() - 7;

            for (Module module : modules) {
                if (!module.isEnabled()) continue;

                float x1 = x - font.getWidth(module.getName());

                Rounds.drawRound(x1 - 2, offset, font.getWidth(module.getName()) + 6, font.getHeight() + 1, 3, Color.BLACK);
                font.draw(module.getName(), x1, offset + 1, -1);
                offset += font.getHeight() + 2.5f;
            }
        }
    }

    private List<Module> getSorted(Font font) {
        List<Module> modules = new ArrayList<>(Quantumclient.getInstance().moduleStorage.getModules());
        modules.sort(Comparator.comparingDouble(module -> -font.getWidth(module.getName())));

        return modules;
    }

/*    @SneakyThrows
    private int getFPS() {
        Class<?> mc = MinecraftClient.class;
        Field field = mc.getDeclaredFields()[91];
        field.setAccessible(true);

        return field.getInt(null);
    }*/
}
