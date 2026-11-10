package rental.exception;

/** Thrown when someone tries to rent a vehicle that is already rented. */
public class VehicleNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VehicleNotAvailableException(String vehicleId) {
        super("Vehicle is not available: " + vehicleId);
    }
}
