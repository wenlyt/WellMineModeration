package ru.nedan.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.nedan.event.impl.EventCloseContainer;
import ru.nedan.event.impl.EventMessage;
import ru.nedan.event.impl.EventMotion;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends PlayerEntity {

    public MixinClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Shadow public abstract void closeScreen();

    @Shadow private boolean lastSprinting;

    @Shadow public abstract boolean isSneaking();

    @Shadow private boolean lastSneaking;

    @Shadow protected abstract boolean isCamera();

    @Shadow private double lastX;
    @Shadow private double lastBaseY;
    @Shadow private double lastZ;
    @Shadow private float lastYaw;
    @Shadow private float lastPitch;
    @Shadow private int ticksSinceLastPositionPacketSent;
    @Shadow private boolean lastOnGround;
    @Shadow private boolean autoJumpEnabled;
    @Shadow @Final protected MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void injSendMessage(String message, CallbackInfo ci) {
        EventMessage eventMessage = new EventMessage(message, true);
        eventMessage.call();

        if (eventMessage.isCanceled()) ci.cancel();
    }

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    public void closeHandledScreen() {
        EventCloseContainer eventCloseContainer = new EventCloseContainer(this.currentScreenHandler.syncId);
        eventCloseContainer.call();

        if (eventCloseContainer.isCanceled()) return;

        this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
        this.closeScreen();
    }

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    private void sendMovementPackets() {
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
            this.lastSprinting = bl;
        }

        boolean bl2 = this.isSneaking();
        if (bl2 != this.lastSneaking) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastSneaking = bl2;
        }

        if (this.isCamera()) {
            EventMotion ev = new EventMotion(this.yaw, this.pitch);

            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = (double) (ev.getYaw() - this.lastYaw);
            double h = (double) (ev.getPitch() - this.lastPitch);
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(vec3d.x, -999.0, vec3d.z, this.yaw, this.pitch, this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.getX(), this.getY(), this.getZ(), ev.getYaw(), ev.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(ev.getYaw(), ev.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }

            if (bl4) {
                this.lastYaw = ev.getYaw();
                this.lastPitch = ev.getPitch();
            }

            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.autoJump;
        }

    }
}
