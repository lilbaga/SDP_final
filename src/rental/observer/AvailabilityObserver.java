package rental.observer;

import rental.model.Vehicle;

/** Pattern: OBSERVER. Role: Observer (the interface that subscribers implement). */
public interface AvailabilityObserver {

    void onVehicleAvailable(Vehicle vehicle);
}
