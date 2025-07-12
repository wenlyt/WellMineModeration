package ru.nedan.util;

import lombok.SneakyThrows;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import ru.nedan.mixin.GameRendererAccessor;

public class Projection implements Wrapper {

    @SneakyThrows
    public static Vec2f project(double x, double y, double z) {
        Vec3d camera_pos = mc.getEntityRenderDispatcher().camera.getPos();
        Quaternion cameraRotation = mc.getEntityRenderDispatcher().getRotation().copy();
        cameraRotation.conjugate();

        Vec3f result3f = new Vec3f((float) (camera_pos.x - x), (float) (camera_pos.y - y), (float) (camera_pos.z - z));
        result3f.rotate(cameraRotation);

        if (mc.options.bobView) {
            Entity renderViewEntity = mc.getCameraEntity();
            if (renderViewEntity instanceof PlayerEntity) {
                calculateViewBobbing((PlayerEntity) renderViewEntity, result3f);
            }
        }

        double fov = ((GameRendererAccessor)mc.gameRenderer).invokeGetFov(mc.gameRenderer.getCamera(), mc.getTickDelta(), true);

        return calculateScreenPosition(result3f, fov);
    }

    private static void calculateViewBobbing(PlayerEntity playerentity, Vec3f result3f) {
        float walked = playerentity.horizontalSpeed;
        float f = walked - playerentity.prevHorizontalSpeed;
        float f1 = -(walked + f * mc.getTickDelta());
        float f2 = MathHelper.lerp(mc.getTickDelta(), playerentity.prevStrideDistance, playerentity.strideDistance);

        Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_X, Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, true);
        quaternion.conjugate();
        result3f.rotate(quaternion);

        Quaternion quaternion1 = new Quaternion(Vec3f.POSITIVE_Z, MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F, true);
        quaternion1.conjugate();
        result3f.rotate(quaternion1);

        Vec3f bobTranslation = new Vec3f((MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F), (-Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2)), 0.0f);
        bobTranslation.set(bobTranslation.getX(), -bobTranslation.getY(), bobTranslation.getZ());
        result3f.add(bobTranslation);
    }

    private static Vec2f calculateScreenPosition(Vec3f result3f, double fov) {
        float halfHeight = mc.getWindow().getScaledHeight() / 2.0F;
        float scaleFactor = halfHeight / (result3f.getZ() * (float) Math.tan(Math.toRadians(fov / 2.0F)));
        if (result3f.getZ() < 0.0F) {
            return new Vec2f(-result3f.getX() * scaleFactor + mc.getWindow().getScaledWidth() / 2.0F, mc.getWindow().getScaledHeight() / 2.0F - result3f.getY() * scaleFactor);
        }
        return new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }

}