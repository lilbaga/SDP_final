package rental.model;

/** Concrete Product of the Factory Method pattern. */
public class Van extends Vehicle {

    private static final double HOURLY_RATE = 12.0;
    private static final double DAILY_RATE = 80.0;

    private final int cargoVolumeM3;

    public Van(String id, String model, int cargoVolumeM3) {
        super(id, model, VehicleType.VAN, HOURLY_RATE, DAILY_RATE);
        this.cargoVolumeM3 = cargoVolumeM3;
    }

    @Override
    public String getDetails() {
        return "cargo " + cargoVolumeM3 + " m3";
    }
}
