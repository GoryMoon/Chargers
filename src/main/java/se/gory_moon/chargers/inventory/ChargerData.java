package se.gory_moon.chargers.inventory;

public interface ChargerData {
    long get(int index);

    void set(int index, long data);

    int getCount();
}
