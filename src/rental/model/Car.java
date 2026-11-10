package rental.model;

/** Concrete Product of the Factory Method pattern. */
public class Car extends Vehicle {

    private static final double HOURLY_RATE = 8.0;
    private static final double DAILY_RATE = 50.0;

    private final int seats;

    public Car(String id, String model, int seats) {
        super(id, model, VehicleType.CAR, HOURLY_RATE, DAILY_RATE);
        this.seats = seats;
    }

    @Override
    public String getDetails() {
        return seats + " seats";
    }
}
