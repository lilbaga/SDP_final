package rental.decorator;

/**
 * The extras that a customer can choose. Every constant knows how to wrap a charge with its own decorator,
 * so the facade needs no switch/if chain (Clean Code ch. 17: replace switch with polymorphism).
 *
 * The order of the constants is the order of wrapping: INSURANCE is the last one on purpose.
 */
public enum Extra {
    GPS {
        @Override
        public RentalCharge wrap(RentalCharge charge) {
            return new GpsDecorator(charge);
        }
    },
    CHILD_SEAT {
        @Override
        public RentalCharge wrap(RentalCharge charge) {
            return new ChildSeatDecorator(charge);
        }
    },
    INSURANCE {
        @Override
        public RentalCharge wrap(RentalCharge charge) {
            return new InsuranceDecorator(charge);
        }
    };

    public abstract RentalCharge wrap(RentalCharge charge);
}
