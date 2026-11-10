package rental.factory;

import rental.model.Bike;
import rental.model.Vehicle;

/** Pattern: FACTORY METHOD. Role: ConcreteCreator (creates a Bike). */
public class BikeFactory extends VehicleFactory {

    @Override
    protected String idPrefix() {
        return "BIKE";
    }

    @Override
    protected Vehicle createVehicle(String id, String model) {
        return new Bike(id, model, true);
    }
}
