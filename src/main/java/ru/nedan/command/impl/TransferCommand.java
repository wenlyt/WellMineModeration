package ru.nedan.command.impl;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import ru.nedan.Quantumclient;
import ru.nedan.command.api.Command;
import ru.nedan.event.impl.EventUpdate;
import ru.nedan.util.ChatUtil;
import ru.nedan.util.TimerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TransferCommand extends Command {

    private String activeAnarchy;
    private final List<Integer> hotbarSlots = new ArrayList<>();

    private final TimerUtils buyTimer = new TimerUtils();
    private int buySize;
    private boolean activeBuy;

    public TransferCommand() {
        super("transfer", ".transfer [anarchy]");
        Quantumclient.getInstance().eventBus.register(this);
    }

    @Override
    public void execute(String... args) {
        try {
            activeAnarchy = args[1];
            transfer();
        } catch (Exception ex) {
            ChatUtil.addMessage(this.getUsage());
        }
    }

    @Subscribe
    public void onTick(EventUpdate e) {
        if (activeBuy) {
            boolean onAuc = mc.currentScreen instanceof GenericContainerScreen;

            if (onAuc && buyTimer.timeElapsed(400)) {
                GenericContainerScreen screen = (GenericContainerScreen) mc.currentScreen;
                buyTimer.updateLast();

                mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
                buySize++;

                if (buySize >= hotbarSlots.size()) {
                    activeBuy = false;
                }
            }
        }
    }

    private void transfer() {
        hotbarSlots.clear();
        setSlots();

        if (hotbarSlots.isEmpty()) {
            ChatUtil.addMessage("У вас нету предметов в хотбаре!");
            return;
        }

        CompletableFuture.runAsync(() -> {
            for (int i : hotbarSlots) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                mc.player.sendChatMessage("/ah dsell 10");
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            mc.player.sendChatMessage("/an" + activeAnarchy);
            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }

            mc.player.sendChatMessage("/ah " + mc.getSession().getUsername());
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            buySize = 0;
            activeBuy = true;
        });
    }

    private void setSlots() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (!stack.isEmpty())
                hotbarSlots.add(i);
        }
    }
}
