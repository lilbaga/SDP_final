package rental.builder;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import rental.decorator.Extra;
import rental.model.Customer;
import rental.model.PricingType;

/**
 * Pattern: BUILDER. Role: Product.
 * An immutable description of what the customer wants. It can be created only by RentalRequestBuilder
 * (the constructor is package-private), so a half-filled request can never exist.
 */
public final class RentalRequest {

    private final Customer customer;
    private final String vehicleId;
    private final PricingType pricingType;
    private final int units;
    private final Set<Extra> extras;

    RentalRequest(Customer customer, String vehicleId, PricingType pricingType, int units, Set<Extra> extras) {
        this.customer = customer;
        this.vehicleId = vehicleId;
        this.pricingType = pricingType;
        this.units = units;
        Set<Extra> copy = EnumSet.noneOf(Extra.class);
        copy.addAll(extras);
        this.extras = Collections.unmodifiableSet(copy);
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public int getUnits() {
        return units;
    }

    /** The extras in the order of the enum, so INSURANCE is always wrapped last. */
    public Set<Extra> getExtras() {
        return extras;
    }

    @Override
    public String toString() {
        return customer + " wants " + vehicleId + " for " + units + " " + pricingType + " unit(s), extras: " + extras;
    }
}
