package rental.factory;

import rental.model.Car;
import rental.model.Vehicle;

/** Pattern: FACTORY METHOD. Role: ConcreteCreator (creates a Car). */
public class CarFactory extends VehicleFactory {

    private static final int DEFAULT_SEATS = 5;

    @Override
    protected String idPrefix() {
        return "CAR";
    }

    @Override
    protected Vehicle createVehicle(String id, String model) {
        return new Car(id, model, DEFAULT_SEATS);
    }
}
