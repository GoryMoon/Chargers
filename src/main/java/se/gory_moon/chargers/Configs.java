package se.gory_moon.chargers;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configs {

    public static final Server SERVER;
    public static final ModConfigSpec serverSpec;

    public static final Common COMMON;
    public static final ModConfigSpec commonSpec;

    static {
        Pair<Server, ModConfigSpec> configSpecPairServer = new ModConfigSpec.Builder().configure(Server::new);
        serverSpec = configSpecPairServer.getRight();
        SERVER = configSpecPairServer.getLeft();

        Pair<Common, ModConfigSpec> configSpecPairCommon = new ModConfigSpec.Builder().configure(Common::new);
        commonSpec = configSpecPairCommon.getRight();
        COMMON = configSpecPairCommon.getLeft();
    }

    public static class Server {
        public Tier tier1;
        public Tier tier2;
        public Tier tier3;
        public Tier tier4;
        public Wireless wireless;

        Server(ModConfigSpec.Builder builder) {
            builder.comment("Chargers configs")
                    .push("chargers");

            builder.push("tier_1");
                tier1 = new Tier(builder, 25000, 500, 500);
            builder.pop();

            builder.push("tier_2");
                tier2 = new Tier(builder, 500000, 10000, 10000);
            builder.pop();

            builder.push("tier_3");
                tier3 = new Tier(builder, 1000000, 25000, 25000);
            builder.pop();

            builder.push("tier_4");
                tier4 = new Tier(builder, 25000000, 625000, 625000);
            builder.pop();

            builder.push("wireless");
                wireless = new Wireless(builder, 200000, 4000, 4000, 24);
            builder.pop(2);
        }

        public static class Tier {
            public ModConfigSpec.LongValue storage;

            public ModConfigSpec.LongValue maxInput;

            public ModConfigSpec.LongValue maxOutput;

            private Tier(ModConfigSpec.Builder builder, int storage, int in, int out) {
                this.storage = builder
                        .comment("The amount of energy the charger can hold")
                        .worldRestart()
                        .defineInRange("storage", storage, 0, Long.MAX_VALUE);

                this.maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", in, 0, Long.MAX_VALUE);

                this.maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", out, 0, Long.MAX_VALUE);
            }
        }

        public static class Wireless {

            public ModConfigSpec.LongValue storage;
            public ModConfigSpec.LongValue maxInput;
            public ModConfigSpec.LongValue maxOutput;
            public ModConfigSpec.IntValue range;

            protected Wireless(ModConfigSpec.Builder builder, int storage, int in, int out, int range) {
                this.storage = builder
                        .comment("The amount of energy the wireless charger can hold")
                        .worldRestart()
                        .defineInRange("storage", storage, 0, Long.MAX_VALUE);

                this.maxInput = builder
                        .comment("The amount of energy/tick that can be inserted")
                        .worldRestart()
                        .defineInRange("max_input", in, 0, Long.MAX_VALUE);

                this.maxOutput = builder
                        .comment("The amount of energy/tick that can be extracted", "This is the max speed items would be charged at, actual speed could be slower depending on item")
                        .worldRestart()
                        .defineInRange("max_output", out, 0, Long.MAX_VALUE);

                this.range = builder
                        .comment("The range from the charger that item will be charged")
                        .defineInRange("range", range, 0, 128);
            }
        }
    }

    public static class Common {
        public ModConfigSpec.BooleanValue curiosCompat;

        Common(ModConfigSpec.Builder builder) {
            builder.comment("Common configs")
                    .push("common");

            builder.comment("Compat configs")
                    .push("compat");

            curiosCompat = builder
                    .comment("If the wireless charger should charge curios items")
                    .define("curios_compat", true);

            builder.pop(2);
        }
    }
}
