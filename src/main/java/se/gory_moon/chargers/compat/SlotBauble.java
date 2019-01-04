package se.gory_moon.chargers.compat;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler {

    private int baubleSlot;
    private EntityPlayer player;

    public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
        this.baubleSlot = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return ((IBaublesItemHandler)getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        ItemStack stack = getStack();
        if(stack.isEmpty() && !stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null))
            return false;

        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble.canUnequip(stack, player);
    }

    @Override
    public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
        if (!getHasStack() && stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(stack, playerIn);
        }
        super.onTake(playerIn, stack);
        return stack;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (getHasStack() && !ItemStack.areItemStacksEqual(stack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
        }

        ItemStack oldStack = getStack().copy();
        super.putStack(stack);

        if (getHasStack() && !ItemStack.areItemStacksEqual(oldStack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onEquipped(getStack(), player);
        }
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
