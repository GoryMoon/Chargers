package se.gory_moon.chargers.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import se.gory_moon.chargers.Constants;
import se.gory_moon.chargers.item.ItemRegistry;

public class ChargerItemModelProvider extends ItemModelProvider {

    public ChargerItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ItemRegistry.CHARGER_T1_ITEM.getId().toString(), modLoc("block/charger_tier_1"));
        withExistingParent(ItemRegistry.CHARGER_T2_ITEM.getId().toString(), modLoc("block/charger_tier_2"));
        withExistingParent(ItemRegistry.CHARGER_T3_ITEM.getId().toString(), modLoc("block/charger_tier_3"));
        withExistingParent(ItemRegistry.CHARGER_T4_ITEM.getId().toString(), modLoc("block/charger_tier_4"));
        withExistingParent(ItemRegistry.CHARGER_CREATIVE_ITEM.getId().toString(), modLoc("block/charger_creative"));

        withExistingParent(ItemRegistry.CHARGER_WIRELESS_ITEM.getId().toString(), modLoc("block/wireless_charger_disabled"));
    }
}
