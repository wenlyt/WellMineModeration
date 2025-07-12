package ru.nedan.gui.component.setting;

import ru.nedan.gui.component.Component;
import ru.nedan.gui.component.ModuleComponent;
import ru.nedan.module.api.setting.Setting;

public class SettingComponent extends Component {
    public final Setting setting;
    public ModuleComponent parent;

    public SettingComponent(Setting setting, ModuleComponent parent, float offset) {
        super(parent.x + 2, parent.y, parent.width - 4, setting.getHeight(), offset);
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public double getFull() {
        return super.getFull() + parent.offset + parent.scrollOffset;
    }
}
