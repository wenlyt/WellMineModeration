package ru.nedan.util;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;

@UtilityClass
public class InventoryUtil implements Wrapper {

    public static final Runnable DEFAULT_USE_RUNNABLE = () -> {
        mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        mc.player.swingHand(Hand.MAIN_HAND);
    };

    public int findSlot(Item item, boolean breakIfFind) {
        int slot = -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (stack.getItem() == item) {
                slot = i;

                if (breakIfFind)
                    break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    public int findSlot(String name, boolean breakIfFind) {
        int slot = -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (ChatUtil.stripTextFormat(stack.getName().getString().toLowerCase()).contains(name.toLowerCase())) {
                slot = i;

                if (breakIfFind)
                    break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    public int findChestplate() {
        int slot = -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStack(i);

            if (isChestplate(stack)) {
                slot = i;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    public void moveItem(int from, int to) {
        mc.interactionManager.clickSlot(0, from, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, to, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, from, 0, SlotActionType.PICKUP, mc.player);
    }

    private boolean isChestplate(ItemStack stack) {
        Item item = stack.getItem();

        return item == Items.LEATHER_CHESTPLATE || item == Items.IRON_CHESTPLATE || item == Items.DIAMOND_CHESTPLATE || item == Items.NETHERITE_CHESTPLATE;
    }

    public void applySlot(int slot, Runnable runnable) {
        if (slot == -1) return;

        if (slot < 36) {
            mc.interactionManager.pickFromInventory(slot);
            runnable.run();
            mc.interactionManager.pickFromInventory(slot);
        } else {
            slot -= 36;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            runnable.run();
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.inventory.selectedSlot));
        }
    }

}
