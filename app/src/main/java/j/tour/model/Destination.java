package j.tour.model;

public class Destination {
    private String destinationId; 
    private String name;
    private String category;
    private String location; 
    private String description;

    public Destination(String destinationId, String name, String category, String location, String description) {
        this.destinationId = destinationId;
        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
    }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}