package rental.model;

/** The kinds of vehicles that the company rents out. */
public enum VehicleType {
    CAR("Car"),
    BIKE("Bike"),
    VAN("Van");

    private final String label;

    VehicleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
