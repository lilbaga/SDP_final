package rental.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rental.exception.VehicleNotFoundException;
import rental.model.Vehicle;

/** Stores all vehicles of the company. The facade owns one repository and hides it from the client. */
public class FleetRepository {

    private final Map<String, Vehicle> vehicles = new LinkedHashMap<>();

    public void add(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
    }

    public Vehicle findById(String vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new VehicleNotFoundException(vehicleId);
        }
        return vehicle;
    }

    public List<Vehicle> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(vehicles.values()));
    }

    public List<Vehicle> findAvailable() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.isAvailable()) {
                result.add(vehicle);
            }
        }
        return result;
    }
}
