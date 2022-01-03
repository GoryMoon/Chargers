package se.gory_moon.chargers.inventory;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.block.BlockRegistry;
import se.gory_moon.chargers.block.entity.CustomItemStackHandler;
import se.gory_moon.chargers.compat.Curios;
import se.gory_moon.chargers.network.PacketHandler;
import se.gory_moon.chargers.network.WindowPropPacket;
import se.gory_moon.chargers.network.WindowPropPacket.SyncPair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.inventory.InventoryMenu.*;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChargerMenu extends AbstractContainerMenu {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] { EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET };
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    @Nullable
    public final IItemHandler curios;
    private final IItemHandler itemHandler;
    private final ContainerData energyData;
    private final ContainerLevelAccess access;
    private final List<DataSlot> customTracked = Lists.newArrayList();
    private final List<ServerPlayer> usingPlayers = new ArrayList<>();
    private final Slot inputSlot;
    private final Slot outputSlot;

    public ChargerMenu(MenuType<ChargerMenu> containerType, int containerId, Inventory inventory) {
        this(containerType, containerId, inventory, new CustomItemStackHandler(2), new SimpleContainerData(6), ContainerLevelAccess.NULL);
    }

    public ChargerMenu(MenuType<ChargerMenu> container, int containerId, Inventory playerInventory, CustomItemStackHandler itemHandler, ContainerData energyData, ContainerLevelAccess access) {
        super(container, containerId);
        this.itemHandler = itemHandler;

        this.energyData = energyData;
        this.access = access;
        this.curios = Curios.getCurios(playerInventory.player);

        int curiosOffset = curios != null ? 9: 0;
        inputSlot = addSlot(new InputSlot(itemHandler, 0, 70 - curiosOffset, 29 + 6));
        outputSlot = addSlot(new OutputSlot(itemHandler, 1, 70 - curiosOffset, 62 + 6));

        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + 6 + i * 18));

        for (i = 0; i < 9; ++i)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + 6));

        for (i = 0; i < 4; ++i) {
            final EquipmentSlot slot = EQUIPMENT_SLOTS[i];
            addSlot(new Slot(playerInventory, 36 + (3 - i), 92 - curiosOffset, 8 + 6 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(slot, playerInventory.player);
                }

                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(player);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slot.getIndex()]);
                }
            });
        }
        addSlot(new Slot(playerInventory, 40, 112 + curiosOffset, 62 + 6) {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        if (curios != null) {
            for (i = 0; i < 7; i++) {
                addSlot(Curios.getSlot(playerInventory.player, curios, i, 103 + (i / 4) * 18, 8 + (i % 4) * 18));
            }
        }
        for (i = 0; i < energyData.getCount(); ++i) {
            customTracked.add(DataSlot.forContainer(energyData, i));
        }
    }

    @Override
    public void broadcastChanges() {
        List<SyncPair> toSync = new ArrayList<>();
        for (int i = 0; i < customTracked.size(); ++i) {
            DataSlot dataSlot = customTracked.get(i);
            if (dataSlot.checkAndClearUpdateFlag())
                toSync.add(new SyncPair(i, dataSlot.get()));
        }

        if (!toSync.isEmpty()) {
            PacketDistributor.PacketTarget target = PacketDistributor.NMLIST.with(usingPlayers.stream().map(serverPlayer -> serverPlayer.connection.connection)::toList);
            PacketHandler.INSTANCE.send(target, new WindowPropPacket(this.containerId, toSync));
        }

        super.broadcastChanges();
    }

    @Override
    public void setData(int id, int data) {
        customTracked.get(id).set(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        int inventoryStart = this.outputSlot.index + 1;
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

            if (index == this.inputSlot.index || index == this.outputSlot.index) {
                /*if (Curios.isLoaded() && (!mergeItemStack(stack, curiosStart, curiosEnd, false) && !mergeItemStack(stack, inventoryStart, armorEnd + 1, true)))
                    return ItemStack.EMPTY;
                else if(!Curios.isLoaded() && ) */
                if (!moveItemStackTo(stack, inventoryStart, offhand, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (!itemHandler.isItemValid(0, stack)) {
                    if (index <= inventoryEnd) {
                        if (!moveItemStackTo(stack, hotbarStart, hotbarEnd + 1, false))
                            return ItemStack.EMPTY;
                    } else if (index < hotbarEnd + 1 && !moveItemStackTo(stack, inventoryStart, inventoryEnd + 1, false))
                        return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, this.inputSlot.index, this.inputSlot.index + 1, false))
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
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T1.get()) || stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T2.get()) || stillValid(access, player, BlockRegistry.CHARGER_BLOCK_T3.get());
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
        return (int) ((double) getEnergy() / (double) getEnergyMax() * length);
    }

    @SubscribeEvent
    public static void onContainerOpened(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof ChargerMenu menu && event.getPlayer() instanceof ServerPlayer serverPlayer) {
            menu.usingPlayers.add(serverPlayer);

            List<SyncPair> toSync = new ArrayList<>();
            for (int i = 0; i < menu.customTracked.size(); ++i)
                toSync.add(new SyncPair(i, menu.customTracked.get(i).get()));

            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new WindowPropPacket(menu.containerId, toSync));
        }
    }

    @SubscribeEvent
    public static void onContainerClosed(PlayerContainerEvent.Close event) {
        if (event.getContainer() instanceof ChargerMenu menu && event.getPlayer() instanceof ServerPlayer serverPlayer)
            menu.usingPlayers.remove(serverPlayer);
    }
}
