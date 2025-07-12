package ru.nedan.module.impl;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import ru.nedan.event.impl.EventUpdate;
import ru.nedan.event.impl.WorldEvent;
import ru.nedan.module.api.Module;
import ru.nedan.module.api.ModuleInfo;
import ru.nedan.util.animation.Animation;
import ru.nedan.util.animation.util.Easings;
import ru.nedan.util.shader.Rounds;

import java.awt.*;

@ModuleInfo(name = "TargetESP", desc = "2Д визуализация клиента")
public class TargetESP extends Module {
    private final Animation alpha = new Animation();
    private PlayerEntity target;

    public TargetESP() {
        alpha.setValue(0.0);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        boolean hasTarget = target != null;

        if (hasTarget) {
            alpha.animate(1.0, 0.3, Easings.QUART_OUT);
        } else {
            alpha.animate(0.0, 0.3, Easings.QUART_OUT);
        }
    }

    @Subscribe
    public void onRender(WorldEvent e) {
        if (target == null || alpha.getValue() <= 0.01) return;

        double x = target.prevX + (target.getX() - target.prevX) * e.getPartialTicks();
        double y = target.prevY + (target.getY() - target.prevY) * e.getPartialTicks();
        double z = target.prevZ + (target.getZ() - target.prevZ) * e.getPartialTicks();

        int alphaValue = (int) (255 * alpha.getValue());
        Color color = new Color(255, 0, 0, alphaValue);

        double size = 1.0;
        double height = target.getHeight();

        Rounds.drawRound(x - size / 2, y, size, size, 0, color);
        Rounds.drawRound(x - size / 2, y + height / 2, size, size, 0, color);
        Rounds.drawRound(x - size / 2, y + height, size, size, 0, color);

        Rounds.drawRound(x - target.getWidth() / 2, y - 0.1, target.getWidth(), 0.1, 0, color);
        Rounds.drawRound(x - target.getWidth() / 2, y + height, target.getWidth(), 0.1, 0, color);
    }

    @Override
    public void onDisable() {
        target = null;
        super.onDisable();
    }

    public void handleTeleportCommand(String playerName) {
        setTarget(playerName);
    }

    private void setTarget(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            this.target = null;
            return;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                this.target = player;
                return;
            }
        }
    }
}