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
        public Tier tier1 = new Tier(25000, 15000, 15000);
        public Tier tier2 = new Tier(500000, 400000, 400000);
        public Tier tier3 = new Tier(1000000, 900000, 900000);
        public Wireless wireless = new Wireless();

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configs")
                    .push("server");

            builder.comment("Chargers configs")
                    .push("chargers");

            builder.push("tier_1");
                tier1.build(builder);
            builder.pop();

            builder.push("tier_2");
                tier2.build(builder);
            builder.pop();

            builder.push("tier_3");
                tier3.build(builder);
            builder.pop();

            builder.push("wireless");
                wireless.build(builder);
            builder.pop(3);
        }

        public static class Tier {
            private final int defaultStorage;
            public ForgeConfigSpec.IntValue storage;

            private final int defaultMaxInput;
            public ForgeConfigSpec.IntValue maxInput;

            private final int defaultMaxOutput;
            public ForgeConfigSpec.IntValue maxOutput;

            private Tier(int defaultStorage, int in, int out) {
                this.defaultStorage = defaultStorage;
                this.defaultMaxInput = in;
                this.defaultMaxOutput = out;
            }

            protected void build(ForgeConfigSpec.Builder builder) {
                storage = builder
                        .comment("The amount of energy the charger can hold")
                        .worldRestart()
                        .defineInRange("storage", defaultStorage, 0, Integer.MAX_VALUE);

                maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", defaultMaxInput, 0, Integer.MAX_VALUE);

                maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", defaultMaxOutput, 0, Integer.MAX_VALUE);
            }
        }

        public static class Wireless {

            public ForgeConfigSpec.IntValue storage;
            public ForgeConfigSpec.IntValue maxInput;
            public ForgeConfigSpec.IntValue maxOutput;
            public ForgeConfigSpec.IntValue range;

            protected void build(ForgeConfigSpec.Builder builder) {
                storage = builder
                        .comment("The amount of energy the wireless charger can hold")
                        .worldRestart()
                        .defineInRange("storage", 200000, 0, Integer.MAX_VALUE);

                maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", 25000, 0, Integer.MAX_VALUE);

                maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", 25000, 0, Integer.MAX_VALUE);

                range = builder
                        .comment("The range from the charger that item will be charged")
                        .defineInRange("range", 24, 0, 128);
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

        }
    }
}
