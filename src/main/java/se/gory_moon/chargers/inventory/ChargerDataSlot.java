package se.gory_moon.chargers.inventory;

public abstract class ChargerDataSlot {
    private long prevValue;

    public ChargerDataSlot() {
    }

    public static ChargerDataSlot forContainer(final ChargerData data, final int index) {
        return new ChargerDataSlot() {
            public long get() {
                return data.get(index);
            }

            public void set(long value) {
                data.set(index, value);
            }
        };
    }

    public abstract long get();

    public abstract void set(long value);

    public boolean checkAndClearUpdateFlag() {
        long tmp = this.get();
        boolean changed = tmp != this.prevValue;
        this.prevValue = tmp;
        return changed;
    }
}
