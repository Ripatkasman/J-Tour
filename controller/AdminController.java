package j.tour.controller;

import j.tour.controller.database.DataStore;
import j.tour.model.Destination;

public class AdminController {
    public void addDestinationToStore(String name, String cat, String loc, String desc) {
        String id = String.valueOf(DataStore.getInstance().getAllDestinations().size() + 1);
        Destination d = new Destination(id, name, cat, loc, desc);
        DataStore.getInstance().getAllDestinations().add(d);
    }

    public void removeDestinationFromStore(Destination d) {
        DataStore.getInstance().getAllDestinations().remove(d);
    }
}
