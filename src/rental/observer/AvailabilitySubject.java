package rental.observer;

/** Pattern: OBSERVER. Role: Subject (register, remove and notify observers). */
public interface AvailabilitySubject {

    void subscribe(AvailabilityObserver observer);

    void unsubscribe(AvailabilityObserver observer);

    void notifyObservers();
}
