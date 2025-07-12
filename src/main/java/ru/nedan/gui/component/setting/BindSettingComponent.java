package ru.nedan.gui.component.setting;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.gui.component.ModuleComponent;
import ru.nedan.module.api.setting.BindSetting;
import ru.nedan.module.api.setting.Setting;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.KeyUtil;
import ru.nedan.util.shader.Rounds;

import java.awt.*;

public class BindSettingComponent extends SettingComponent {

    private boolean binding;

    public BindSettingComponent(Setting setting, ModuleComponent parent, float offset) {
        super(setting, parent, offset);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (setting.isVisible()) {
            BindSetting bindSetting = (BindSetting) this.setting;

            Font f = Fonts.Inter.get(14);

            f.draw(setting.getName(), x + 2, getFull() + 2, -1);

            String keyText = KeyUtil.getKeyName(bindSetting.getKey());
            String text = binding ? "..." : (keyText == null ? "None" : keyText);

            double x1 = x + width - 7 - f.getWidth(text);

            Rounds.drawRound(x1 - 2, getFull() + 1, f.getWidth(text) + 6, f.getHeight() + 2, 4, Color.BLACK);
            f.draw(text, x1, getFull() + 2, -1);
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        BindSetting bindSetting = (BindSetting) this.setting;
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                bindSetting.setKey(0);
            } else {
                bindSetting.setKey(keyCode);
            }
            binding = false;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        BindSetting bindSetting = (BindSetting) this.setting;

        if (binding) {
            bindSetting.setKey(400 + button);
//            ChatUtil.addMessage("Сеттинг " + bindSetting.getName() + " успешо ");
            binding = false;
            return;
        }

        Font f = Fonts.Inter.get(14);
        String keyText = KeyUtil.getKeyName(bindSetting.getKey());
        String text = binding ? "..." : (keyText == null ? "None" : keyText);

        double x1 = x + width - 7 - f.getWidth(text);
        Rounds.drawRound(x1 - 2, getFull() + 1, f.getWidth(text) + 6, f.getHeight() + 2, 4, Color.BLACK);

        if (mouseX >= x1 -2 && mouseY >= getFull() + 1 && mouseX <= x1 -2 + f.getWidth(text) + 6 && mouseY <= getFull() + 1 + f.getHeight() + 2) {
            binding = true;
        }
    }
}
