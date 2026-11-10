package rental.factory;

import rental.model.Van;
import rental.model.Vehicle;

/** Pattern: FACTORY METHOD. Role: ConcreteCreator (creates a Van). */
public class VanFactory extends VehicleFactory {

    private static final int DEFAULT_CARGO_VOLUME_M3 = 10;

    @Override
    protected String idPrefix() {
        return "VAN";
    }

    @Override
    protected Vehicle createVehicle(String id, String model) {
        return new Van(id, model, DEFAULT_CARGO_VOLUME_M3);
    }
}
