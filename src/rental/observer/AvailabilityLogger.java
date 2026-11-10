package rental.observer;

import rental.model.Vehicle;

/**
 * Pattern: OBSERVER. Role: ConcreteObserver.
 * A second, different observer: it writes a log line for the fleet manager.
 * Vehicle does not know anything about this class, only about the AvailabilityObserver interface.
 */
public class AvailabilityLogger implements AvailabilityObserver {

    private int eventCount = 0;

    @Override
    public void onVehicleAvailable(Vehicle vehicle) {
        eventCount++;
        System.out.println("   [LOG #" + eventCount + "] " + vehicle.getId() + " returned to the fleet");
    }

    public int getEventCount() {
        return eventCount;
    }
}
