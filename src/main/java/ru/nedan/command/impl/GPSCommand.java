package ru.nedan.command.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import ru.nedan.Quantumclient;
import ru.nedan.command.api.Command;
import ru.nedan.event.impl.EventRender2D;
import ru.nedan.fonts.Font;
import ru.nedan.fonts.Fonts;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.Projection;
import ru.nedan.util.shader.Rounds;

import java.awt.*;

public class GPSCommand extends Command {

    private PlayerEntity gpsEntity;

    public GPSCommand() {
        super("gps", ".gps [x] [y] [z] | .gps off");
        Quantumclient.getInstance().eventBus.register(this);
    }

    @Override
    public void execute(String... args) {
        try {
            if (args[1].equalsIgnoreCase("off")) {
                gpsEntity = null;
                ChatUtil.addMessage("GPS успешно выключен!");
            } else {
                double x, y, z;

                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    ChatUtil.addMessage("Вы неправильно ввели координаты");
                    return;
                }

                gpsEntity = new PlayerEntity(mc.world, new BlockPos(x, y, z), 90, mc.player.getGameProfile()) {
                    @Override
                    public boolean isSpectator() {
                        return true;
                    }

                    @Override
                    public boolean isCreative() {
                        return true;
                    }
                };

                ChatUtil.addMessage(String.format("Установил GPS на координатах: %s %s %s", x, y, z));
            }
        } catch (Exception ex) {
            ChatUtil.addMessage(this.getUsage());
        }
    }

    @Subscribe
    public void onRender2D(EventRender2D e) {
        if (!isActive()) return;

        Vec2f position = Projection.project(gpsEntity.getX(), gpsEntity.getY(), gpsEntity.getZ());

        Font f = Fonts.Cygra.get(14);

        String text = "До точки: " + String.format("%.1f", mc.player.distanceTo(gpsEntity));

        float width = f.getWidth(text) + 7;
        float height = f.getHeight() + 4;

        float x = position.x - width / 2f;
        float y = position.y - height / 2f;

        Rounds.drawRound(x, y, width, height, 4, Color.BLACK);
        f.draw(text, x + 2.5, y + 2.5, -1);
    }

    private boolean isActive() {
        return gpsEntity != null;
    }

}
