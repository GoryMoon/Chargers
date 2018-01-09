package se.gory_moon.chargers;

import net.minecraftforge.common.config.Config;
import se.gory_moon.chargers.lib.ModInfo;

@Config(modid = ModInfo.MODID)
public class Configs {

    @Config.Comment("Chargers configs")
    public static Chargers chargers = new Chargers();

    @Config.Comment("Compat configs")
    public static Compat compat = new Compat();

    public static class Chargers {

        public Tier tier1 = new Tier(25000, 15000, 15000);
        public Tier tier2 = new Tier(500000, 400000, 400000);
        public Tier tier3 = new Tier(1000000, 900000, 900000);
        public Wireless wireless = new Wireless();

        public static class Tier {
            @Config.Comment("The amount of energy the fast charger can hold")
            @Config.RangeInt(min = 0)
            public int storage = 25000;

            @Config.Comment("The amount of energy/tick that can be inserted")
            @Config.RangeInt(min = 0)
            public int maxInput = 15000;

            @Config.Comment({"The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item"})
            @Config.RangeInt(min = 0)
            public int maxOutput = 15000;

            public Tier(int storage, int in, int out) {
                this.storage = storage;
                this.maxInput = in;
                this.maxOutput = out;
            }
        }

        public static class Wireless {
            @Config.Comment("The amount of energy the fast charger can hold")
            @Config.RangeInt(min = 0)
            public int wirelessStorage = 200000;

            @Config.Comment("The amount of energy/tick that can be inserted")
            @Config.RangeInt(min = 0)
            public int wirelessMaxInput = 25000;

            @Config.Comment({"The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item"})
            @Config.RangeInt(min = 0)
            public int wirelessMaxOutput = 25000;

            @Config.Comment("The range from the charger that item will be charged")
            @Config.RangeInt(min = 1, max = 100)
            public int wirelessRange = 24;
        }
    }

    public static class Compat {

        @Config.Comment("If baubles compat should be enabled")
        public boolean baublesCompat = true;
    }
}
