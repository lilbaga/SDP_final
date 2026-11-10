package rental.builder;

import java.util.EnumSet;
import java.util.Set;
import rental.decorator.Extra;
import rental.model.Customer;
import rental.model.PricingType;

/**
 * Pattern: BUILDER. Role: Builder.
 * A rental request has required data (customer, vehicle, duration) and optional data (extras).
 * Instead of one long constructor with five parameters, the client fills the request step by step
 * with readable method names, and build() checks that nothing important is missing.
 */
public class RentalRequestBuilder {

    private Customer customer;
    private String vehicleId;
    private PricingType pricingType;
    private int units;
    private final Set<Extra> extras = EnumSet.noneOf(Extra.class);

    public RentalRequestBuilder forCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public RentalRequestBuilder vehicle(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public RentalRequestBuilder hourly(int hours) {
        return duration(PricingType.HOURLY, hours);
    }

    public RentalRequestBuilder daily(int days) {
        return duration(PricingType.DAILY, days);
    }

    public RentalRequestBuilder withExtra(Extra extra) {
        extras.add(extra);
        return this;
    }

    public RentalRequest build() {
        if (customer == null) {
            throw new IllegalStateException("Rental request needs a customer");
        }
        if (vehicleId == null) {
            throw new IllegalStateException("Rental request needs a vehicle");
        }
        if (pricingType == null) {
            throw new IllegalStateException("Rental request needs a duration (hourly or daily)");
        }
        return new RentalRequest(customer, vehicleId, pricingType, units, extras);
    }

    private RentalRequestBuilder duration(PricingType pricingType, int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Rental duration must be positive, but was " + units);
        }
        this.pricingType = pricingType;
        this.units = units;
        return this;
    }
}
