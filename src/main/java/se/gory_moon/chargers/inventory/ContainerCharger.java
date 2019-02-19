package se.gory_moon.chargers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import se.gory_moon.chargers.compat.Baubles;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.tile.TileEntityCharger;

public class ContainerCharger extends Container {

    private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    private TileEntityCharger tileCharger;
    public IItemHandler baubles;

    public ContainerCharger(EntityPlayer player, TileEntityCharger tile) {
        InventoryPlayer inventory = player.inventory;
        tileCharger = tile;
        baubles = Baubles.getBaubles(player);

        int baublesOffset = baubles != null ? 9: 0;
        addSlotToContainer(new SlotInput(tile.inventoryHandler, 0, 70 - baublesOffset, 29));
        addSlotToContainer(new SlotOutput(tile.inventoryHandler, 1, 70 - baublesOffset, 62));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));

        for(i = 0; i < 4; ++i) {
            final EntityEquipmentSlot slot = EQUIPMENT_SLOTS[i];
            addSlotToContainer(new Slot(inventory, 36 + (3 - i), 92 - baublesOffset, 8 + i * 18) {
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
        addSlotToContainer(new Slot(inventory, 40, 112 + baublesOffset, 62) {
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        if (baubles != null) {
            for (i = 0; i < 7; i++) {
                addSlotToContainer(Baubles.getSlot(player, baubles, i, 103 + (i / 4) * 18, 8 + (i % 4) * 18));
            }
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        PacketHandler.sendToListeningPlayers(listeners, tileCharger.getUpdatePacket());
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
        int offhand = armorEnd + 1;
        int baublesStart = offhand + 1;
        int baublesEnd = baublesStart + 6;

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            if (index == 1 || index == 0) {
                if (Baubles.isLoaded() && (!mergeItemStack(stack, baublesStart, baublesEnd, false) && !mergeItemStack(stack, inventoryStart, armorEnd + 1, true)))
                    return ItemStack.EMPTY;
                else if (!Baubles.isLoaded() && !mergeItemStack(stack, inventoryStart, armorEnd + 1, true))
                    return ItemStack.EMPTY;
                slot.onSlotChange(stack, itemstack);
            } else {
                if (!tileCharger.inventoryHandler.isItemValid(0, stack)) {
                    if (index <= inventoryEnd) {
                        if (!mergeItemStack(stack, hotbarStart, hotbarEnd + 1, false))
                            return ItemStack.EMPTY;
                    } else if (index < hotbarEnd + 1 && !mergeItemStack(stack, inventoryStart, inventoryEnd + 1, false))
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
