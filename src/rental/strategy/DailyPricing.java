package rental.strategy;

import rental.model.Vehicle;

/**
 * Pattern: STRATEGY. Role: ConcreteStrategy.
 * Price = days x daily rate. Long rentals (7 days and more) get a discount.
 */
public class DailyPricing implements PricingStrategy {

    private static final int LONG_RENTAL_DAYS = 7;
    private static final double LONG_RENTAL_DISCOUNT = 0.10;

    @Override
    public double calculatePrice(Vehicle vehicle, int days) {
        double price = days * vehicle.getDailyRate();
        if (days >= LONG_RENTAL_DAYS) {
            price = price * (1 - LONG_RENTAL_DISCOUNT);
        }
        return price;
    }

    @Override
    public String getName() {
        return "Daily pricing";
    }

    @Override
    public String getUnitName() {
        return "day";
    }
}
