package se.gory_moon.chargers.inventory;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.IItemHandler;
import se.gory_moon.chargers.blocks.BlockRegistry;
import se.gory_moon.chargers.compat.Curios;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.network.WindowPropPacket;
import se.gory_moon.chargers.tile.CustomItemStackHandler;

import java.util.List;

import static net.minecraft.inventory.container.PlayerContainer.*;

public class ContainerCharger extends Container {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{ EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlotType[] EQUIPMENT_SLOTS = new EquipmentSlotType[]{ EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
    public final IItemHandler curios;
    private final IItemHandler itemHandler;
    private final IIntArray energyData;
    private final IWorldPosCallable pos;
    private final List<IntReferenceHolder> customTracked = Lists.newArrayList();

    public ContainerCharger(ContainerType<ContainerCharger> containerType, int windowId, PlayerInventory inventory) {
        this(containerType, windowId, inventory, new CustomItemStackHandler(2), new IntArray(6), IWorldPosCallable.NULL);
    }
    public ContainerCharger(ContainerType<ContainerCharger> container, int windowId, PlayerInventory playerInventory, CustomItemStackHandler itemHandler, IIntArray energyData, IWorldPosCallable pos) {
        super(container, windowId);
        this.itemHandler = itemHandler;

        this.energyData = energyData;
        this.pos = pos;
        PlayerEntity player = playerInventory.player;
        curios = Curios.getCurios(player);

        int baublesOffset = curios != null ? 9: 0;
        addSlot(new SlotInput(itemHandler, 0, 70 - baublesOffset, 29 + 6));
        addSlot(new SlotOutput(itemHandler, 1, 70 - baublesOffset, 62 + 6));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + 6 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + 6));

        for(i = 0; i < 4; ++i) {
            final EquipmentSlotType slot = EQUIPMENT_SLOTS[i];
            addSlot(new Slot(playerInventory, 36 + (3 - i), 92 - baublesOffset, 8 + 6 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(slot, player);
                }

                public boolean mayPickup(PlayerEntity player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slot.getIndex()]);
                }
            });
        }
        addSlot(new Slot(playerInventory, 40, 112 + baublesOffset, 62 + 6) {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        if (curios != null) {
            for (i = 0; i < 7; i++) {
                addSlot(Curios.getSlot(player, curios, i, 103 + (i / 4) * 18, 8 + (i % 4) * 18));
            }
        }
        for(i = 0; i < energyData.getCount(); ++i) {
            customTracked.add(IntReferenceHolder.forContainer(energyData, i));
        }
    }

    @Override
    public void broadcastChanges() {
        for(int j = 0; j < customTracked.size(); ++j) {
            IntReferenceHolder intreferenceholder = customTracked.get(j);
            if (intreferenceholder.checkAndClearUpdateFlag()) {
                PacketHandler.sendToListeningPlayers(containerListeners, PacketHandler.INSTANCE.toVanillaPacket(new WindowPropPacket(this.containerId, j, intreferenceholder.get()), NetworkDirection.PLAY_TO_CLIENT));
            }
        }

        super.broadcastChanges();
    }

    @Override
    public void setData(int id, int data) {
        customTracked.get(id).set(data);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        int inventoryStart = 2;
        int inventoryEnd = inventoryStart + 26;
        int hotbarStart = inventoryEnd + 1;
        int hotbarEnd = hotbarStart + 8;
        int armorStart = hotbarEnd + 1;
        int armorEnd = armorStart + 3;
        int offhand = armorEnd + 1;
        int curiosStart = offhand + 1;
        int curiosEnd = curiosStart + 6;

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index == 1 || index == 0) {
                /*if (Curios.isLoaded() && (!mergeItemStack(stack, curiosStart, curiosEnd, false) && !mergeItemStack(stack, inventoryStart, armorEnd + 1, true)))
                    return ItemStack.EMPTY;
                else */
                if (/*!Curios.isLoaded() && */!moveItemStackTo(stack, inventoryStart, armorEnd + 1, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (!itemHandler.isItemValid(0, stack)) {
                    if (index <= inventoryEnd) {
                        if (!moveItemStackTo(stack, hotbarStart, hotbarEnd + 1, false))
                            return ItemStack.EMPTY;
                    } else if (index < hotbarEnd + 1 && !moveItemStackTo(stack, inventoryStart, inventoryEnd + 1, false))
                        return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, 0, 1, false))
                    return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();

            if (stack.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(pos, player, BlockRegistry.CHARGER_BLOCK_T1.get()) || stillValid(pos, player, BlockRegistry.CHARGER_BLOCK_T2.get()) || stillValid(pos, player, BlockRegistry.CHARGER_BLOCK_T3.get());
    }

    private static final int
        ENERGY = 0,
        ENERGY_MAX = 1,
        MAX_IN = 2,
        MAX_OUT = 3,
        AVERAGE_IN = 4,
        AVERAGE_OUT = 5;

    public boolean hasEnergy() {
        return getEnergy() > 0;
    }

    public int getEnergy() {
        return energyData.get(ENERGY);
    }

    public int getEnergyMax() {
        return energyData.get(ENERGY_MAX);
    }

    public int getMaxIn() {
        return energyData.get(MAX_IN);
    }

    public int getMaxOut() {
        return energyData.get(MAX_OUT);
    }

    public int getAverageIn() {
        return energyData.get(AVERAGE_IN);
    }

    public int getAverageOut() {
        return energyData.get(AVERAGE_OUT);
    }

    public int getEnergyDiff() {
        return getAverageIn() - getAverageOut();
    }

    public int getEnergyScaled(int length) {
        return (int) ((double)getEnergy() / (double) getEnergyMax() * length);
    }
}
