package ru.nedan.gui.component;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.gui.ClickGuiScreen;
import ru.nedan.gui.component.setting.BindSettingComponent;
import ru.nedan.gui.component.setting.BooleanSettingComponent;
import ru.nedan.gui.component.setting.SettingComponent;
import ru.nedan.module.api.Module;
import ru.nedan.module.api.setting.BindSetting;
import ru.nedan.module.api.setting.BooleanSetting;
import ru.nedan.module.api.setting.Setting;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.KeyUtil;
import ru.nedan.util.Scissor;
import ru.nedan.util.animation.Animation;
import ru.nedan.util.animation.util.Easings;
import ru.nedan.util.shader.Rounds;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends Component {
    private final Module module;
    private boolean binding, extended;
    private double defaultHeight;
    private final Animation heightAnimation = new Animation();
    private final Animation toggleAnimation = new Animation();

    private final List<SettingComponent> components;

    public ModuleComponent(Module module, ClickGuiScreen clickGuiScreen, float offset) {
        super(clickGuiScreen.getX() + 4, clickGuiScreen.getY() + 4, clickGuiScreen.getWidth() - 8, 17, offset);
        this.module = module;
        this.defaultHeight = this.height;

        heightAnimation.setToValue(height).setValue(height);
        toggleAnimation.setValue(module.isEnabled() ? 1.0 : 0.0);

        components = new ArrayList<>();

        float setOffset = (float) height;

        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                components.add(new BooleanSettingComponent(setting, this, setOffset));
            } else if (setting instanceof BindSetting) {
                components.add(new BindSettingComponent(setting, this, setOffset));
            }

            setOffset += (float) setting.getHeight();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        heightAnimation.update();
        toggleAnimation.update();

        height = heightAnimation.getValue();

        // Анимация переключателя
        if (module.isEnabled() && toggleAnimation.getValue() < 1.0) {
            toggleAnimation.animate(1.0, 0.25, Easings.QUART_OUT);
        } else if (!module.isEnabled() && toggleAnimation.getValue() > 0.0) {
            toggleAnimation.animate(0.0, 0.25, Easings.QUART_OUT);
        }

        // Переключатель слева (уменьшенный)
        int toggleWidth = 20;
        int toggleHeight = 10;
        int toggleX = (int)(x + 6);
        int toggleY = (int)(getFull() + (defaultHeight - toggleHeight) / 2);

        // Получаем значение анимации (0.0 - выключен, 1.0 - включен)
        double animationValue = toggleAnimation.getValue();

        // Цвета переключателя
        Color enabledColor = new Color(76, 175, 80); // Зеленый когда включен
        Color disabledColor = new Color(120, 120, 120); // Серый когда выключен
        Color circleColor = Color.WHITE;

        // Интерполяция цвета фона переключателя
        Color backgroundColor = interpolateColor(disabledColor, enabledColor, animationValue);

        // Рисуем фон переключателя
        Rounds.drawRound(toggleX, toggleY, toggleWidth, toggleHeight, 5, backgroundColor);

        // Позиция кружочка (уменьшенный)
        int circleRadius = 4;
        int circleX = (int)(toggleX + circleRadius + 1 + (toggleWidth - circleRadius * 2 - 2) * animationValue);
        int circleY = toggleY + toggleHeight / 2;

        // Рисуем кружочек
        Rounds.drawRound(circleX - circleRadius, circleY - circleRadius, circleRadius * 2, circleRadius * 2, circleRadius, circleColor);

        Font f = Fonts.Inter.get(16);

        String keyName = KeyUtil.getKeyName(module.getKey());
        String text = binding ? "Bind: " + (keyName == null ? "None" : keyName) : module.getName();

        // Рисуем название модуля (сдвигаем вправо из-за переключателя)
        f.draw(text, x + toggleWidth + 12, getFull() + 3, module.isEnabled() ? Color.WHITE.getRGB() : new Color(180, 180, 180).getRGB());

        Scissor.push();

        Scissor.setFromComponentCoordinates(x, getFull(), width, height);

        for (SettingComponent settingComponent : components)
            settingComponent.render(matrixStack, mouseX, mouseY);

        Scissor.pop();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (extended)
            for (SettingComponent settingComponent : components)
                settingComponent.mouseClicked(mouseX, mouseY, button);

        if (isHovered(mouseX, mouseY)) {
            switch (button) {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    extended = !extended;

                    double height = defaultHeight;

                    if (extended)
                        for (Setting setting : module.getSettings())
                            if (setting.isVisible())
                                height += setting.getHeight();

                    heightAnimation.animate(height, 0.2, Easings.QUAD_OUT);

                    break;
                case 2:
                    binding = true;
                    break;
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        for (SettingComponent settingComponent : components)
            settingComponent.keyPressed(keyCode);

        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                module.setKey(0);
                ChatUtil.addMessage("Модуль " + module.getName() + " успешно разбинжен!");
            } else {
                module.setKey(keyCode);
                ChatUtil.addMessage("Модуль " + module.getName() + " успешно забинжен на клавишу: " + KeyUtil.getKeyName(keyCode));
            }

            binding = false;
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= getFull() && mouseX <= x + width && mouseY <= getFull() + defaultHeight;
    }

    private Color interpolateColor(Color color1, Color color2, double factor) {
        factor = Math.max(0, Math.min(1, factor));

        int red = (int) (color1.getRed() + factor * (color2.getRed() - color1.getRed()));
        int green = (int) (color1.getGreen() + factor * (color2.getGreen() - color1.getGreen()));
        int blue = (int) (color1.getBlue() + factor * (color2.getBlue() - color1.getBlue()));
        return new Color(red, green, blue);
    }
}