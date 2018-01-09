package se.gory_moon.chargers.items;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

public class ItemChargerBlock extends ItemMultiTexture {

    public ItemChargerBlock(Block p_i47262_1_, Block p_i47262_2_, Mapper p_i47262_3_) {
        super(p_i47262_1_, p_i47262_2_, p_i47262_3_);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getMetadata() < 0 || stack.getMetadata() > 4)
            return EnumRarity.COMMON;
        return EnumRarity.values()[stack.getMetadata()];
    }
}
