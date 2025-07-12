package ru.nedan.gui.component;

import net.minecraft.client.util.math.MatrixStack;

public class Component {
    public double x, y, width, height;
    public float offset, scrollOffset;

    public Component(double x, double y, double width, double height, float offset) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    public void mouseReleased() {

    }

    public void keyPressed(int keyCode) {

    }

    public double getFull() {
        return y + offset + scrollOffset;
    }
}
