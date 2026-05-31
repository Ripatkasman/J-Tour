package j.tour.controller;

import j.tour.model.Destination;
import j.tour.model.DayPlan;
import j.tour.model.TripPlan;

public class TripController {
    private TripPlan currentTripPlan;

    public TripController() {
        // Membuat rancangan TripPlan default saat controller dibuat
        this.currentTripPlan = new TripPlan("Rencana Liburan Jogja");
    }

    /**
     * Mengatur ulang durasi hari perjalanan di dalam rencana aktif
     */
    public void setTripDuration(int totalDays) {
        if (currentTripPlan != null) {
            currentTripPlan.setDurationDays(totalDays);
        }
    }

    /**
     * Memasukkan sebuah destinasi ke dalam list hari tertentu (Hari 1, Hari 2, dst)
     */
    public void addDestinationToDay(Destination d, int dayNum) {
        if (currentTripPlan == null || currentTripPlan.getDayPlans() == null) {
            return;
        }

        // Mencari objek DayPlan yang nomor harinya cocok dengan pilihan user
        for (DayPlan day : currentTripPlan.getDayPlans()) {
            if (day.getDayNumber() == dayNum) {
                day.addItem(d); // Memasukkan destinasi ke hari tersebut
                break;
            }
        }
    }

    /**
     * Mengambil data rencana perjalanan aktif saat ini
     */
    public TripPlan getCurrentTripPlan() {
        return currentTripPlan;
    }
}