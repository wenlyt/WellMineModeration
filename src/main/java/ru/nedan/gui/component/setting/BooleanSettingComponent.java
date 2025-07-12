package ru.nedan.gui.component.setting;

import net.minecraft.client.util.math.MatrixStack;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.gui.component.ModuleComponent;
import ru.nedan.module.api.setting.BooleanSetting;
import ru.nedan.module.api.setting.Setting;
import ru.nedan.util.animation.Animation;
import ru.nedan.util.animation.util.Easings;
import ru.nedan.util.shader.Rounds;

import java.awt.*;

public class BooleanSettingComponent extends SettingComponent {
    private Animation xAnim = new Animation();

    public BooleanSettingComponent(Setting setting, ModuleComponent parent, float offset) {
        super(setting, parent, offset);
        double x = this.x + width - 20;
        BooleanSetting booleanSetting = (BooleanSetting) this.setting;
        double x1 = booleanSetting.isToggle() ? x + 7.5 : x;
        xAnim.setValue(x1).setToValue(x1);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (setting.isVisible()) {
            xAnim.update();
            Font f = Fonts.Inter.get(14);

            f.draw(setting.getName(), x + 2, getFull() + 2, -1);

            double x = this.x + width - 20;

            Rounds.drawRound(x, getFull() + 2, 15, 8, 4, new Color(0x333333));

            BooleanSetting booleanSetting = (BooleanSetting) this.setting;

            double x1 = booleanSetting.isToggle() ? x + 7.5 : x;
            xAnim.animate(x1, 0.2, Easings.QUAD_OUT);

            Rounds.drawRound(xAnim.getValue(), getFull() + 2, 7.5, 7.5, 4, Color.WHITE);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        double x = this.x + width - 20;
        BooleanSetting booleanSetting = (BooleanSetting) this.setting;
        if (mouseX >= x && mouseX <= x + 15 && mouseY >= getFull() + 2 && mouseY <= getFull() + 8) {
            booleanSetting.setToggle(!booleanSetting.isToggle());
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}
