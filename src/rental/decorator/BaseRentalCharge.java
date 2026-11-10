package rental.decorator;

import rental.model.Vehicle;
import rental.strategy.PricingStrategy;

/**
 * Pattern: DECORATOR. Role: ConcreteComponent (the object that gets wrapped).
 * The price of the vehicle itself is calculated by a Strategy, so the two patterns work together.
 */
public class BaseRentalCharge implements RentalCharge {

    private final Vehicle vehicle;
    private final PricingStrategy pricingStrategy;
    private final int units;

    public BaseRentalCharge(Vehicle vehicle, PricingStrategy pricingStrategy, int units) {
        this.vehicle = vehicle;
        this.pricingStrategy = pricingStrategy;
        this.units = units;
    }

    @Override
    public String getDescription() {
        return String.format("%s for %d %s(s), %s",
                vehicle, units, pricingStrategy.getUnitName(), pricingStrategy.getName());
    }

    @Override
    public double getCost() {
        return pricingStrategy.calculatePrice(vehicle, units);
    }
}
