package rental.model;

import rental.decorator.RentalCharge;
import rental.util.Money;

/** One rental contract: who rents which vehicle and how much it costs. */
public class Rental {

    private final int id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final RentalCharge charge;
    private boolean active = true;

    public Rental(int id, Customer customer, Vehicle vehicle, RentalCharge charge) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.charge = charge;
    }

    public void close() {
        active = false;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public RentalCharge getCharge() {
        return charge;
    }

    public boolean isActive() {
        return active;
    }

    public String getSummary() {
        return String.format("Rental #%d | %s | %s | total %s",
                id, customer.getName(), charge.getDescription(), Money.format(charge.getCost()));
    }
}
