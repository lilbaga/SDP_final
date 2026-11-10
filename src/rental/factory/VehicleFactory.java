package rental.factory;

import rental.model.Vehicle;

public abstract class VehicleFactory {

    private int counter = 0;

    public Vehicle create(String model) {
        String id = idPrefix() + "-" + String.format("%03d", ++counter);
        return createVehicle(id, model);
    }

    protected abstract String idPrefix();

    /** The factory method. */
    protected abstract Vehicle createVehicle(String id, String model);
}
