package rental.decorator;

/** Pattern: DECORATOR. Role: ConcreteDecorator. Adds a GPS navigator for a fixed price. */
public class GpsDecorator extends RentalChargeDecorator {

    private static final double GPS_PRICE = 5.0;

    public GpsDecorator(RentalCharge wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + GPS";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + GPS_PRICE;
    }
}
