package rental.strategy;

import rental.model.Vehicle;

/**
 * Pattern: STRATEGY. Role: Strategy (the common interface of all price algorithms).
 */
public interface PricingStrategy {

    double calculatePrice(Vehicle vehicle, int units);

    /** Name of the pricing scheme, for example "Hourly pricing". */
    String getName();

    /** Name of one billing unit: "hour" or "day". */
    String getUnitName();
}
