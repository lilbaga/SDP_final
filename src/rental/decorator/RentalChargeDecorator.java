package rental.decorator;

public abstract class RentalChargeDecorator implements RentalCharge {

    protected final RentalCharge wrapped;

    protected RentalChargeDecorator(RentalCharge wrapped) {
        this.wrapped = wrapped;
    }
}
