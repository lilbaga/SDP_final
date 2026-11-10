package rental.exception;

/** Thrown when a rental id does not exist. */
public class RentalNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RentalNotFoundException(int rentalId) {
        super("Rental not found: #" + rentalId);
    }
}
