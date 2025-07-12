package ru.nedan.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.nedan.util.Rotation;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow protected abstract Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw);

    @Shadow public float yaw;

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    public void updateVelocity(float speed, Vec3d movementInput) {
        Vec3d vec3d = movementInputToVelocity(movementInput, speed, Rotation.yawOffset == Integer.MIN_VALUE ? this.yaw : Rotation.yawOffset);
        this.setVelocity(this.getVelocity().add(vec3d));
    }
}
