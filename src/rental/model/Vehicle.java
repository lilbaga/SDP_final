package rental.model;

import java.util.ArrayList;
import java.util.List;
import rental.exception.VehicleNotAvailableException;
import rental.observer.AvailabilityObserver;
import rental.observer.AvailabilitySubject;

/**
 * Base class of every vehicle (Car, Bike, Van) = the "Product" of the Factory Method pattern.
 *
 * It is also the "ConcreteSubject" of the Observer pattern: when the vehicle is returned,
 * it notifies all subscribed observers.
 */
public abstract class Vehicle implements AvailabilitySubject {

    private final String id;
    private final String model;
    private final VehicleType type;
    private final double hourlyRate;
    private final double dailyRate;
    private boolean available = true;
    private final List<AvailabilityObserver> observers = new ArrayList<>();

    protected Vehicle(String id, String model, VehicleType type, double hourlyRate, double dailyRate) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }

    /** Short technical description, every subclass has its own (seats, cargo volume, ...). */
    public abstract String getDetails();

    public void markRented() {
        if (!available) {
            throw new VehicleNotAvailableException(id);
        }
        available = false;
    }

    /** The vehicle is free again: this is the event that all observers are waiting for. */
    public void markReturned() {
        available = true;
        notifyObservers();
    }

    @Override
    public void subscribe(AvailabilityObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(AvailabilityObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // iterate over a copy, so an observer may unsubscribe while it is being notified
        for (AvailabilityObserver observer : new ArrayList<>(observers)) {
            observer.onVehicleAvailable(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getType() {
        return type;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return type.getLabel() + " " + model + " (" + id + ")";
    }
}
