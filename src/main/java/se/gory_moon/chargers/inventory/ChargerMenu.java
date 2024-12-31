package se.gory_moon.chargers.inventory;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.block.entity.BlockEntityRegistry;
import se.gory_moon.chargers.block.entity.ChargerItemStackHandler;
import se.gory_moon.chargers.network.WindowPropPayload;
import se.gory_moon.chargers.network.WindowPropPayload.SyncPair;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.inventory.InventoryMenu.*;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChargerMenu extends AbstractContainerMenu {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final ResourceLocation EMPTY_CHARGE_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "item/empty_charge_slot");

    private final IItemHandler itemHandler;
    private final ChargerData energyData;
    private final ContainerLevelAccess access;
    private final List<ChargerDataSlot> customTracked = Lists.newArrayList();
    private final List<ServerPlayer> usingPlayers = new ArrayList<>();
    private final Slot inputSlot;
    private final Slot outputSlot;
    private final Slot chargeSlot;

    public ChargerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new ChargerItemStackHandler(), new SimpleChargerData(7), ContainerLevelAccess.NULL);
    }

    public ChargerMenu(int containerId, Inventory playerInventory, ChargerItemStackHandler itemHandler, ChargerData energyData, ContainerLevelAccess access) {
        super(BlockEntityRegistry.CHARGER_CONTAINER.get(), containerId);
        this.itemHandler = itemHandler;

        this.energyData = energyData;
        this.access = access;

        inputSlot = addSlot(new InputSlot(itemHandler, 0, 70, 35));
        outputSlot = addSlot(new OutputSlot(itemHandler, 1, 70, 68));
        chargeSlot = addSlot(new InputSlot(itemHandler, 2, 44, 89).setBackground(BLOCK_ATLAS, EMPTY_CHARGE_SLOT));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 117 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 175));

        for (i = 0; i < 4; ++i) {
            final EquipmentSlot slot = EQUIPMENT_SLOTS[i];
            addSlot(new Slot(playerInventory, 36 + (3 - i), 96, 14 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.canEquip(slot, playerInventory.player);
                }

                public boolean mayPickup(@NotNull Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) && super.mayPickup(player);
                }
            }.setBackground(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slot.getIndex()]));
        }
        addSlot(new Slot(playerInventory, 40, 116, 68).setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));

        for (i = 0; i < energyData.getCount(); ++i)
            customTracked.add(ChargerDataSlot.forContainer(energyData, i));
    }

    @Override
    public void broadcastChanges() {
        List<SyncPair> toSync = new ArrayList<>();
        for (int i = 0; i < customTracked.size(); ++i) {
            ChargerDataSlot dataSlot = customTracked.get(i);
            if (dataSlot.checkAndClearUpdateFlag())
                toSync.add(new SyncPair(i, dataSlot.get()));
        }

        if (!toSync.isEmpty()) {
            var payload = new WindowPropPayload(this.containerId, toSync);
            for (ServerPlayer player : usingPlayers) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }

        super.broadcastChanges();
    }

    @Override
    public void setData(int id, int data) {
        customTracked.get(id).set(data);
    }

    public void setData(int id, long data) {
        customTracked.get(id).set(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        int inventoryStart = this.chargeSlot.index + 1;
        int inventoryEnd = inventoryStart + 26;
        int hotbarStart = inventoryEnd + 1;
        int hotbarEnd = hotbarStart + 8;
        int armorStart = hotbarEnd + 1;
        int armorEnd = armorStart + 3;
        int offhand = armorEnd + 1;
        int curiosStart = offhand + 1;
        int curiosEnd = curiosStart + 6;

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index == this.inputSlot.index || index == this.outputSlot.index || index == this.chargeSlot.index) {
                /*if (Curios.isLoaded() && (!mergeItemStack(stack, curiosStart, curiosEnd, false) && !mergeItemStack(stack, inventoryStart, armorEnd + 1, true)))
                    return ItemStack.EMPTY;
                else if(!Curios.isLoaded() && ) */
                if (!moveItemStackTo(stack, inventoryStart, offhand, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (!itemHandler.isItemValid(0, stack) && !itemHandler.isItemValid(2, stack)) {
                    if (index <= inventoryEnd) {
                        if (!moveItemStackTo(stack, hotbarStart, hotbarEnd + 1, false))
                            return ItemStack.EMPTY;
                    } else if (index < hotbarEnd + 1 && !moveItemStackTo(stack, inventoryStart, inventoryEnd + 1, false))
                        return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, this.inputSlot.index, this.chargeSlot.index + 1, false))
                    return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
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
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T1.get()) ||
                stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T2.get()) ||
                stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T3.get()) ||
                stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T4.get()) ||
                stillValid(access, player, BlockRegistry.CHARGER_BLOCK_CREATIVE.get());
    }

    private static final int ENERGY = 0, ENERGY_MAX = 1, MAX_IN = 2, MAX_OUT = 3, AVERAGE_IN = 4, AVERAGE_OUT = 5, CREATIVE = 6;

    public boolean hasEnergy() {
        return getEnergy() > 0;
    }

    public long getEnergy() {
        return energyData.get(ENERGY);
    }

    public long getEnergyMax() {
        return energyData.get(ENERGY_MAX);
    }

    public long getMaxIn() {
        return energyData.get(MAX_IN);
    }

    public long getMaxOut() {
        return energyData.get(MAX_OUT);
    }

    public long getAverageIn() {
        return energyData.get(AVERAGE_IN);
    }

    public long getAverageOut() {
        return energyData.get(AVERAGE_OUT);
    }

    public long getEnergyDiff() {
        return getAverageIn() - getAverageOut();
    }

    public int getEnergyScaled(int length) {
        return (int) ((double) getEnergy() / (double) getEnergyMax() * length);
    }

    public boolean isCreative() {
        return energyData.get(CREATIVE) == 1;
    }

    @SubscribeEvent
    public static void onContainerOpened(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof ChargerMenu menu && event.getEntity() instanceof ServerPlayer serverPlayer) {
            menu.usingPlayers.add(serverPlayer);

            List<SyncPair> toSync = new ArrayList<>();
            for (int i = 0; i < menu.customTracked.size(); ++i)
                toSync.add(new SyncPair(i, menu.customTracked.get(i).get()));

            PacketDistributor.sendToPlayer(serverPlayer, new WindowPropPayload(menu.containerId, toSync));
        }
    }

    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event) {
        if (event.getContainer() instanceof ChargerMenu menu && event.getEntity() instanceof ServerPlayer serverPlayer)
            menu.usingPlayers.remove(serverPlayer);
    }
}
