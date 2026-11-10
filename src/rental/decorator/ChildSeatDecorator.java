package rental.decorator;

/** Pattern: DECORATOR. Role: ConcreteDecorator. Adds a child seat for a fixed price. */
public class ChildSeatDecorator extends RentalChargeDecorator {

    private static final double CHILD_SEAT_PRICE = 3.0;

    public ChildSeatDecorator(RentalCharge wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + child seat";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + CHILD_SEAT_PRICE;
    }
}
