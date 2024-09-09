package se.gory_moon.chargers.inventory;

public class SimpleChargerData implements ChargerData {
    private final long[] data;

    public SimpleChargerData(int size) {
        this.data = new long[size];
    }

    @Override
    public long get(int index) {
        return this.data[index];
    }

    @Override
    public void set(int index, long value) {
        this.data[index] = value;
    }

    @Override
    public int getCount() {
        return this.data.length;
    }
}
