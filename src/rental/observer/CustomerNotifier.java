package rental.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rental.model.Customer;
import rental.model.Vehicle;

/** Pattern: OBSERVER. Role: ConcreteObserver. Tells a waiting customer that the vehicle is free again. */
public class CustomerNotifier implements AvailabilityObserver {

    private final Customer customer;
    private final List<String> inbox = new ArrayList<>();

    public CustomerNotifier(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void onVehicleAvailable(Vehicle vehicle) {
        String message = "Dear " + customer.getName() + ", " + vehicle + " is available again!";
        inbox.add(message);
        System.out.println("   [SMS to " + customer.getName() + "] " + message);
    }

    public List<String> getInbox() {
        return Collections.unmodifiableList(inbox);
    }
}
