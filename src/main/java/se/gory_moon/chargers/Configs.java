package se.gory_moon.chargers;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class Configs {

    public static final Server SERVER;
    public static final ForgeConfigSpec serverSpec;

    public static final Common COMMON;
    public static final ForgeConfigSpec commonSpec;

    static {
        Pair<Server, ForgeConfigSpec> configSpecPairServer = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = configSpecPairServer.getRight();
        SERVER = configSpecPairServer.getLeft();

        Pair<Common, ForgeConfigSpec> configSpecPairCommon = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = configSpecPairCommon.getRight();
        COMMON = configSpecPairCommon.getLeft();
    }

    public static class Server {
        public Tier tier1;
        public Tier tier2;
        public Tier tier3;
        public Wireless wireless;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Chargers configs")
                    .push("chargers");

            builder.push("tier_1");
                tier1 = new Tier(builder, 25000, 500, 500);
            builder.pop();

            builder.push("tier_2");
                tier2 = new Tier(builder, 500000, 10000, 10000);
            builder.pop();

            builder.push("tier_3");
                tier3 = new Tier(builder, 1000000, 50000, 50000);
            builder.pop();

            builder.push("wireless");
                wireless = new Wireless(builder, 200000, 4000, 4000, 24);
            builder.pop(2);
        }

        public static class Tier {
            public ForgeConfigSpec.IntValue storage;

            public ForgeConfigSpec.IntValue maxInput;

            public ForgeConfigSpec.IntValue maxOutput;

            private Tier(ForgeConfigSpec.Builder builder, int storage, int in, int out) {
                this.storage = builder
                        .comment("The amount of energy the charger can hold")
                        .worldRestart()
                        .defineInRange("storage", storage, 0, Integer.MAX_VALUE);

                this.maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", in, 0, Integer.MAX_VALUE);

                this.maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", out, 0, Integer.MAX_VALUE);
            }
        }

        public static class Wireless {

            public ForgeConfigSpec.IntValue storage;
            public ForgeConfigSpec.IntValue maxInput;
            public ForgeConfigSpec.IntValue maxOutput;
            public ForgeConfigSpec.IntValue range;

            protected Wireless(ForgeConfigSpec.Builder builder, int storage, int in, int out, int range) {
                this.storage = builder
                        .comment("The amount of energy the wireless charger can hold")
                        .worldRestart()
                        .defineInRange("storage", storage, 0, Integer.MAX_VALUE);

                this.maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", in, 0, Integer.MAX_VALUE);

                this.maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", out, 0, Integer.MAX_VALUE);

                this.range = builder
                        .comment("The range from the charger that item will be charged")
                        .defineInRange("range", range, 0, 128);
            }
        }
    }

    public static class Common {
        public ForgeConfigSpec.BooleanValue curiosCompat;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configs")
                    .push("common");

            builder.comment("Compat configs")
                    .push("compat");

            curiosCompat = builder
                    .comment("If curios compat should be enabled")
                    .define("curios_compat", true);

            builder.pop(2);
        }
    }
}
