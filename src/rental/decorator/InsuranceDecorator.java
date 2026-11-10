package rental.decorator;

public class InsuranceDecorator extends RentalChargeDecorator {

    private static final double INSURANCE_RATE = 0.15;

    public InsuranceDecorator(RentalCharge wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + insurance";
    }

    @Override
    public double getCost() {
        return wrapped.getCost() * (1 + INSURANCE_RATE);
    }
}
