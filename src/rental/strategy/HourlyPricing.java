package rental.strategy;

import rental.model.Vehicle;

/** Pattern: STRATEGY. Role: ConcreteStrategy. Price = hours x hourly rate of the vehicle. */
public class HourlyPricing implements PricingStrategy {

    @Override
    public double calculatePrice(Vehicle vehicle, int hours) {
        return hours * vehicle.getHourlyRate();
    }

    @Override
    public String getName() {
        return "Hourly pricing";
    }

    @Override
    public String getUnitName() {
        return "hour";
    }
}
