package j.tour.model;

import java.util.ArrayList;
import java.util.List;

public class DayPlan {
    private int dayNumber;
    private List<Destination> items;

    public DayPlan(int dayNumber) {
        this.dayNumber = dayNumber;
        this.items = new ArrayList<>();
    }

    public void addItem(Destination dest) {
        items.add(dest);
    }

    public List<Destination> getItems() { return items; }
    public int getDayNumber() { return dayNumber; }
}