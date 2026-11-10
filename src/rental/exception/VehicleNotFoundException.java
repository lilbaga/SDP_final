package rental.exception;

/** Thrown when a vehicle id does not exist in the fleet (we never return null, see Clean Code ch. 7). */
public class VehicleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VehicleNotFoundException(String vehicleId) {
        super("Vehicle not found: " + vehicleId);
    }
}
