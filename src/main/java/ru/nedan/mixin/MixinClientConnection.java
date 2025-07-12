package ru.nedan.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.nedan.event.impl.EventPacket;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

    @Shadow private Channel channel;

    @Shadow
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
    }

    @Shadow private PacketListener packetListener;
    @Shadow private int packetsReceivedCounter;

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", cancellable = true)
    private void injSend(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        EventPacket eventPacket = new EventPacket(packet, true);
        eventPacket.call();

        if (eventPacket.isCanceled())
            ci.cancel();
    }

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            EventPacket eventPacket = new EventPacket(packet, false);
            eventPacket.call();

            if (eventPacket.isCanceled()) return;

            try {
                handlePacket(packet, this.packetListener);
            } catch (OffThreadException var4) {
            }

            ++this.packetsReceivedCounter;
        }

    }
}
