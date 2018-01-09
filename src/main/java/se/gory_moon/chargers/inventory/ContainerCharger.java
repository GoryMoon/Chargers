package se.gory_moon.chargers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.chargers.tile.TileEntityCharger;

public class ContainerCharger extends Container {

    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    private TileEntityCharger tileCharger;
    private int lastCharge;
    private int lastDiff;

    public ContainerCharger(final InventoryPlayer inventory, TileEntityCharger tile) {
        tileCharger = tile;

        addSlotToContainer(new SlotInput(tile.inventoryHandler, 0, 80, 29));
        addSlotToContainer(new SlotOutput(tile.inventoryHandler, 1, 80, 62));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));

        EntityPlayer player = inventory.player;
        for(i = 0; i < 4; ++i) {
            final EntityEquipmentSlot slot = EQUIPMENT_SLOTS[i];
            addSlotToContainer(new Slot(inventory, 36 + (3 - i), 102, 8 + i * 18) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return !stack.isEmpty() && stack.getItem().isValidArmor(stack, slot, player);
                }

                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, tileCharger.storage.getEnergyStored());
        listener.sendWindowProperty(this, 1, tileCharger.getEnergyDiff());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener: listeners) {
            if (lastCharge != tileCharger.storage.getEnergyStored())
                listener.sendWindowProperty(this, 0, tileCharger.storage.getEnergyStored());
            if (lastDiff != tileCharger.getEnergyDiff())
                listener.sendWindowProperty(this, 1, tileCharger.getEnergyDiff());
        }

        lastCharge = tileCharger.storage.getEnergyStored();
        lastDiff = tileCharger.getEnergyDiff();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            tileCharger.storage.setEnergy(data);
        else if (id == 1)
            tileCharger.setEnergyDiff(data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        int inventoryStart = 2;
        int inventoryEnd = inventoryStart + 26;
        int hotbarStart = inventoryEnd + 1;
        int hotbarEnd = hotbarStart + 8;
        int armorStart = hotbarEnd + 1;
        int armorEnd = armorStart + 3;

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            if (index == 1 || index == 0) {
                if (!mergeItemStack(stack, inventoryStart, armorEnd + 1, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(stack, itemstack);
            } else {
                if (!stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    if (index <= inventoryEnd) {
                        if (!mergeItemStack(stack, hotbarStart, hotbarEnd + 1, false))
                            return ItemStack.EMPTY;
                    } else if (index >= inventoryEnd + 1 && index < hotbarEnd + 1 && !mergeItemStack(stack, inventoryStart, inventoryEnd + 1, false))
                        return ItemStack.EMPTY;
                } else if (!mergeItemStack(stack, 0, 1, false))
                    return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (stack.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq((double) tileCharger.getPos().getX() + 0.5D, (double) tileCharger.getPos().getY() + 0.5D, (double) tileCharger.getPos().getZ() + 0.5D) <= 64.0D;
    }
}
