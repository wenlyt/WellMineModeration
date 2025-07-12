package ru.nedan.util;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@UtilityClass
public class Scissor implements Wrapper {
    private static class State implements Cloneable {
        public boolean enabled;
        public int transX;
        public int transY;
        public int x;
        public int y;
        public int width;
        public int height;

        @Override
        public State clone() {
            try {
                return (State) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }

    private static State state = new State();

    private static final List<State> stateStack = Lists.newArrayList();

    public static void push() {
        stateStack.add(state.clone());
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
    }

    public static void pop() {
        state = stateStack.remove(stateStack.size() - 1);
        GL11.glPopAttrib();
    }

    public static void unset() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        state.enabled = false;
    }

    public static void setFromComponentCoordinates(int x, int y, int width, int height) {
        Window res = mc.getWindow();
        double scaleFactor = res.getScaleFactor();

        double screenX = x * scaleFactor;
        double screenY = y * scaleFactor;
        double screenWidth = width * scaleFactor;
        double screenHeight = height * scaleFactor;
        screenY = res.getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height) {
        Window res = mc.getWindow();
        int scaleFactor = (int) res.getScaleFactor();

        int screenX = (int) (x * scaleFactor);
        int screenY = (int) (y * scaleFactor);
        int screenWidth = (int) (width * scaleFactor);
        int screenHeight = (int) (height * scaleFactor);
        screenY = res.getHeight() - screenY - screenHeight;
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void setFromComponentCoordinates(double x, double y, double width, double height, float scale) {
        Window res = mc.getWindow();

        float animationValue = scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        double testX = x + (width * halfAnimationValueRest);
        double testY = y + (height * halfAnimationValueRest);
        double testW = width * animationValue;
        double testH = height * animationValue;

        testX = testX * animationValue + ((res.getScaledWidth() - testW) * halfAnimationValueRest);

        // Получаем коэффициент масштабирования GUI
        float guiScale = (float) res.getScaleFactor();

        // Применяем guiScale для расчёта координат и размеров с учётом масштаба
        int screenX = (int) (testX * guiScale);
        int screenY = (int) (testY * guiScale);
        int screenWidth = (int) (testW * guiScale);
        int screenHeight = (int) (testH * guiScale);

        // Корректируем координаты по Y для нижней части экрана
        screenY = res.getHeight() - screenY - screenHeight;

        // Устанавливаем значения
        set(screenX, screenY, screenWidth, screenHeight);
    }

    public static void set(double x, double y, double width, double height) {
        Window res = mc.getWindow();
        Rectangle screen = new Rectangle(0, 0, res.getWidth(),
                res.getHeight());
        Rectangle current;
        if (state.enabled) {
            current = new Rectangle(state.x, state.y, state.width, state.height);
        } else {
            current = screen;
        }
        Rectangle target = new Rectangle((int) (x + state.transX), (int) (y + state.transY), (int) width, (int) height);
        Rectangle result = current.intersection(target);
        result = result.intersection(screen);
        if (result.width < 0)
            result.width = 0;
        if (result.height < 0)
            result.height = 0;
        state.enabled = true;
        state.x = result.x;
        state.y = result.y;
        state.width = result.width;
        state.height = result.height;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(result.x, result.y, result.width, result.height);
    }

    public static void translate(int x, int y) {
        state.transX = x;
        state.transY = y;
    }

    public static void translateFromComponentCoordinates(int x, int y) {
        Window res = mc.getWindow();
        int totalHeight = res.getScaledHeight();
        int scaleFactor = (int) res.getScaleFactor();

        int screenX = x * scaleFactor;
        int screenY = y * scaleFactor;
        screenY = (totalHeight * scaleFactor) - screenY;
        translate(screenX, screenY);
    }

}
