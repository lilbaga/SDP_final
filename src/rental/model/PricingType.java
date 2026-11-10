package rental.model;

/** How the customer wants to pay: per hour or per day. The facade maps it to a PricingStrategy. */
public enum PricingType {
    HOURLY,
    DAILY
}
