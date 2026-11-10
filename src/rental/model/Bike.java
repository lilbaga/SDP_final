package rental.model;

/** Concrete Product of the Factory Method pattern. */
public class Bike extends Vehicle {

    private static final double HOURLY_RATE = 3.0;
    private static final double DAILY_RATE = 15.0;

    private final boolean helmetIncluded;

    public Bike(String id, String model, boolean helmetIncluded) {
        super(id, model, VehicleType.BIKE, HOURLY_RATE, DAILY_RATE);
        this.helmetIncluded = helmetIncluded;
    }

    @Override
    public String getDetails() {
        return helmetIncluded ? "helmet included" : "no helmet";
    }
}
