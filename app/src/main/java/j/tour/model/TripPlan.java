package j.tour.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripPlan {
    private String tripId;
    private String tripName;
    private LocalDate startDate;
    private List<DayPlan> dayPlans;

    public TripPlan(String tripName) {
        this.tripName = tripName;
        this.dayPlans = new ArrayList<>();
    }

    public TripPlan() {
        //TODO Auto-generated constructor stub
    }

    public void setDurationDays(int totalDays) {
        this.dayPlans.clear(); 
        for (int i = 1; i <= totalDays; i++) {
            this.dayPlans.add(new DayPlan(i));
        }
    }

    // ==========================================
    // GETTER AND SETTER (Untuk menghilangkan warning kuning)
    // ==========================================
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<DayPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }
}