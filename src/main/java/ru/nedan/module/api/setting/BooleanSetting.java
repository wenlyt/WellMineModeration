package ru.nedan.module.api.setting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import ru.nedan.fonts.Fonts;
import ru.nedan.util.MathUtils;
import ru.nedan.util.Wrapper;
import ru.nedan.util.animation.Animation;
import ru.nedan.util.animation.util.Easings;
import ru.nedan.util.shader.Rounds;

import java.awt.*;
import java.util.function.Supplier;

@Getter
@Setter
public class BooleanSetting extends Setting implements Wrapper {
    private boolean toggle;
    private Animation toggleAnimation;

    public BooleanSetting(String name, boolean toggle, Supplier<Boolean> visible) {
        super(name, visible);
        this.toggle = toggle;
        this.toggleAnimation = new Animation();
        this.toggleAnimation.setValue(toggle ? 1.0 : 0.0);
    }

    public boolean get() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        this.toggleAnimation.animate(toggle ? 1.0 : 0.0, 0.25, Easings.QUART_OUT);
    }

    public void toggle() {
        setToggle(!toggle);
    }

    public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY) {
        toggleAnimation.update();

        int toggleWidth = 40;
        int toggleHeight = 20;
        int toggleX = x + width - toggleWidth - 10;
        int toggleY = y + (height - toggleHeight) / 2;

        double animationValue = toggleAnimation.getValue();

        Color enabledColor = new Color(76, 175, 80);
        Color disabledColor = new Color(120, 120, 120);
        Color circleColor = Color.WHITE;

        Color backgroundColor = interpolateColor(disabledColor, enabledColor, animationValue);

        Rounds.drawRound(toggleX, toggleY, toggleWidth, toggleHeight, 10, backgroundColor);

        int circleRadius = 8;
        int circleX = (int) (toggleX + circleRadius + 2 + (toggleWidth - circleRadius * 2 - 4) * animationValue);
        int circleY = toggleY + toggleHeight / 2;

        Rounds.drawRound(circleX - circleRadius, circleY - circleRadius, circleRadius * 2, circleRadius * 2, circleRadius, circleColor);

        Fonts.Cygra.get(14).draw(getName(), x + 5, y + (height - 14) / 2, -1);
    }

    public boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        int toggleWidth = 40;
        int toggleHeight = 20;
        int toggleX = x + width - toggleWidth - 10;
        int toggleY = y + (height - toggleHeight) / 2;

        return mouseX >= toggleX && mouseX <= toggleX + toggleWidth &&
                mouseY >= toggleY && mouseY <= toggleY + toggleHeight;
    }

    public void onClick(int mouseX, int mouseY, int x, int y, int width, int height) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            toggle();
        }
    }

    private Color interpolateColor(Color color1, Color color2, double factor) {
        factor = MathHelper.clamp(factor, 0.0, 1.0);

        int red = (int) (color1.getRed() + factor * (color2.getRed() - color1.getRed()));
        int green = (int) (color1.getGreen() + factor * (color2.getGreen() - color1.getGreen()));
        int blue = (int) (color1.getBlue() + factor * (color2.getBlue() - color1.getBlue()));
        return new Color(red, green, blue);
    }

    @Override
    public void addConfig(JsonObject jsonObject) {
        JsonObject thisObject = new JsonObject();
        thisObject.addProperty("toggle", this.toggle);
        jsonObject.add(this.getName(), thisObject);
    }

    @Override
    public void readFromConfig(JsonObject jsonObject) {
        JsonObject thisObject = jsonObject.get(this.getName()).getAsJsonObject();
        this.toggle = thisObject.get("toggle").getAsBoolean();
        this.toggleAnimation.setValue(toggle ? 1.0 : 0.0);
    }
}