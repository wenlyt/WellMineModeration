package ru.nedan.module.impl;

import org.lwjgl.glfw.GLFW;
import ru.nedan.gui.ClickGuiScreen;
import ru.nedan.module.api.Module;
import ru.nedan.module.api.ModuleInfo;

@ModuleInfo(name = "ClickGUI", desc = "Гуи настройки функций клиента")
public class ClickGUI extends Module {

    public ClickGUI() {
        setKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public void onEnable() {
        mc.openScreen(new ClickGuiScreen());
        super.onEnable();
        this.toggle();
    }
}
