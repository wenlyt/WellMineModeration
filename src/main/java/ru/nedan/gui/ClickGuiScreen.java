package ru.nedan.gui;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import ru.nedan.Quantumclient;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.gui.component.ModuleComponent;
import ru.nedan.module.api.Module;
import ru.nedan.util.MathUtils;
import ru.nedan.util.Scissor;
import ru.nedan.util.Wrapper;
import ru.nedan.util.shader.Rounds;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen implements Wrapper {
    @Getter
    private double x, y, width, height;
    private List<ModuleComponent> moduleComponents;
    private float scroll, scrollAnimated;
    private int selectedCategory = 0;
    private String[] categories = {"A", "G", "B"};
    private int expandedButton = -1;
    private float[] buttonHeightAnimated = {25, 25};
    private float openAnimation = 0;
    private boolean opening = true;
    private String[] themes = {"Фиолетовый", "Красный", "Зелёный", "Синий", "Бирюзовый", "Огненный"};
    private int selectedTheme = 0;

    public ClickGuiScreen() {
        super(new LiteralText(""));
        create();
    }

    public void close() {
        mc.openScreen(null);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        create();
        super.resize(client, width, height);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (opening) {
            openAnimation = MathUtils.lerp(openAnimation, 1.0f, 12);
        } else {
            openAnimation = MathUtils.lerp(openAnimation, 0.0f, 12);
            if (openAnimation < 0.01f) {
                this.close();
                return;
            }
        }

        double animatedWidth = width * openAnimation;
        double animatedHeight = height * openAnimation;
        double animatedX = x + (width - animatedWidth) / 2;
        double animatedY = y + (height - animatedHeight) / 2;

        Rounds.drawRound(animatedX, animatedY, animatedWidth, animatedHeight, 14, new Color(0x111111));

        if (opening && openAnimation > 0.3f) {
            Rounds.drawRound(animatedX, animatedY, 30 * openAnimation, animatedHeight, 14, new Color(0x0A0A0A));
            Rounds.drawRound(animatedX + 25 * openAnimation, animatedY, 5 * openAnimation, animatedHeight, 0, new Color(0x0A0A0A));

            Font f = Fonts.Cygra.get(20);
            Font iconFont = Fonts.Icons.get(24);
            Font buttonFont = Fonts.Cygra.get(14);

            String text = "HarmProduct";
            double centeredX = animatedX + (animatedWidth - f.getWidth(text)) / 2;

            f.draw(text, centeredX, animatedY + 4, -1);

            double categoryX = animatedX + 6;
            double categoryY = animatedY + 40;

            for (int i = 0; i < categories.length; i++) {
                Color textColor = selectedCategory == i ? Color.WHITE : new Color(0x555555);
                iconFont.draw(categories[i], categoryX, categoryY + i * 35, textColor.getRGB());
            }

            double contentX = animatedX + 35;
            double contentY = animatedY + 40;

            if (selectedCategory == 0) {
                Rounds.drawRound(contentX, contentY, 80 * openAnimation, 25, 6, new Color(0x333333));
                buttonFont.draw("4.31", contentX + 5, contentY + 8, -1);

                Rounds.drawRound(contentX, contentY + 30, 80 * openAnimation, 25, 6, new Color(0x333333));
                buttonFont.draw("5.5", contentX + 5, contentY + 38, -1);
            } else if (selectedCategory == 1) {
                float targetHeight0 = expandedButton == 0 ? 50 : 25;
                buttonHeightAnimated[0] = MathUtils.lerp(buttonHeightAnimated[0], targetHeight0, 15);

                Rounds.drawRound(contentX, contentY, 80 * openAnimation, buttonHeightAnimated[0], 6, new Color(0x333333));
                buttonFont.draw("4.31", contentX + 5, contentY + 8, -1);
                if (buttonHeightAnimated[0] > 30) {
                    float alpha = (buttonHeightAnimated[0] - 30) / 20.0f;
                    Color descColor = new Color(0xAA, 0xAA, 0xAA, (int)(255 * alpha));
                    buttonFont.draw("пон пон описание", contentX + 5, contentY + 28, descColor.getRGB());
                }

                double nextButtonY = contentY + buttonHeightAnimated[0] + 5;
                float targetHeight1 = expandedButton == 1 ? 50 : 25;
                buttonHeightAnimated[1] = MathUtils.lerp(buttonHeightAnimated[1], targetHeight1, 15);

                Rounds.drawRound(contentX, nextButtonY, 80 * openAnimation, buttonHeightAnimated[1], 6, new Color(0x333333));
                buttonFont.draw("5.5", contentX + 5, nextButtonY + 8, -1);
                if (buttonHeightAnimated[1] > 30) {
                    float alpha = (buttonHeightAnimated[1] - 30) / 20.0f;
                    Color descColor = new Color(0xAA, 0xAA, 0xAA, (int)(255 * alpha));
                    buttonFont.draw("пон пон описание", contentX + 5, nextButtonY + 28, descColor.getRGB());
                }
            } else if (selectedCategory == 2) {
                scrollAnimated = MathUtils.lerp(scrollAnimated, scroll, 8);

                float moduleHeight = 10;

                Scissor.push();

                Scissor.setFromComponentCoordinates(animatedX + 35, animatedY + 30, animatedWidth - 45, animatedHeight - 40);

                for (ModuleComponent component : moduleComponents) {
                    component.scrollOffset = scrollAnimated;
                    component.x = animatedX + 35;
                    component.render(matrices, mouseX, mouseY);
                    moduleHeight += (float) component.height;
                }

                Scissor.pop();

                scroll = MathHelper.clamp(scroll, -moduleHeight, 0);
                updateOffsets();

                double themeX = animatedX + animatedWidth - 120;
                double themeY = animatedY + animatedHeight - 80;
                Rounds.drawRound(themeX, themeY, 110, 70, 8, new Color(0x222222));

                buttonFont.draw("Темы:", themeX + 5, themeY + 5, -1);

                for (int i = 0; i < Math.min(themes.length, 4); i++) {
                    Color themeColor = selectedTheme == i ? Color.WHITE : new Color(0x888888);
                    buttonFont.draw(themes[i], themeX + 5, themeY + 20 + i * 12, themeColor.getRGB());
                }
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (openAnimation < 0.8f) return false;

        double categoryX = x + 6;
        double categoryY = y + 40;
        double categoryWidth = 20;
        double categoryHeight = 30;

        for (int i = 0; i < categories.length; i++) {
            if (mouseX >= categoryX && mouseX <= categoryX + categoryWidth &&
                    mouseY >= categoryY + i * 35 && mouseY <= categoryY + i * 35 + categoryHeight) {
                selectedCategory = i;
                expandedButton = -1;
                scroll = 0;
                scrollAnimated = 0;
                return true;
            }
        }

        double contentX = x + 35;
        double contentY = y + 40;

        if (selectedCategory == 0) {
            if (mouseX >= contentX && mouseX <= contentX + 80 && mouseY >= contentY && mouseY <= contentY + 25) {
                mc.player.sendChatMessage("/123");
                return true;
            }
            if (mouseX >= contentX && mouseX <= contentX + 80 && mouseY >= contentY + 30 && mouseY <= contentY + 55) {
                mc.player.sendChatMessage("/123");
                return true;
            }
        } else if (selectedCategory == 1) {
            if (mouseX >= contentX && mouseX <= contentX + 80 && mouseY >= contentY && mouseY <= contentY + buttonHeightAnimated[0]) {
                expandedButton = expandedButton == 0 ? -1 : 0;
                return true;
            }
            double nextButtonY = contentY + buttonHeightAnimated[0] + 5;
            if (mouseX >= contentX && mouseX <= contentX + 80 && mouseY >= nextButtonY && mouseY <= nextButtonY + buttonHeightAnimated[1]) {
                expandedButton = expandedButton == 1 ? -1 : 1;
                return true;
            }
        } else if (selectedCategory == 2) {
            double themeX = x + width - 120;
            double themeY = y + height - 80;

            if (mouseX >= themeX && mouseX <= themeX + 110 && mouseY >= themeY + 20 && mouseY <= themeY + 70) {
                int themeIndex = (int) ((mouseY - themeY - 20) / 12);
                if (themeIndex >= 0 && themeIndex < Math.min(themes.length, 4)) {
                    selectedTheme = themeIndex;
                    return true;
                }
            }

            for (ModuleComponent moduleComponent : moduleComponents)
                moduleComponent.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selectedCategory == 2) {
            for (ModuleComponent moduleComponent : moduleComponents)
                moduleComponent.mouseReleased();
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (selectedCategory == 2 && mouseX >= x + 35) {
            scroll += (float) (amount * 12f);
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            opening = false;
            return true;
        }

        if (selectedCategory == 2) {
            for (ModuleComponent moduleComponent : moduleComponents)
                moduleComponent.keyPressed(keyCode);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void create() {
        Window window = mc.getWindow();

        width = 400;
        height = 250;

        x = (window.getScaledWidth() - width) / 2;
        y = (window.getScaledHeight() - height) / 2;

        moduleComponents = new ArrayList<>();

        float offset = 30;

        for (Module module : Quantumclient.getInstance().moduleStorage.getModules()) {
            moduleComponents.add(new ModuleComponent(module, this, offset));
            offset += 21;
        }
    }

    private void updateOffsets() {
        float offset = 30;

        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.offset = offset;
            offset += (float) (moduleComponent.height + 4);
        }
    }
}