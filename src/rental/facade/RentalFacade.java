package rental.facade;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rental.builder.RentalRequest;
import rental.decorator.BaseRentalCharge;
import rental.decorator.Extra;
import rental.decorator.RentalCharge;
import rental.exception.RentalNotFoundException;
import rental.factory.BikeFactory;
import rental.factory.CarFactory;
import rental.factory.VanFactory;
import rental.factory.VehicleFactory;
import rental.model.PricingType;
import rental.model.Rental;
import rental.model.Vehicle;
import rental.model.VehicleType;
import rental.observer.AvailabilityObserver;
import rental.repository.FleetRepository;
import rental.strategy.DailyPricing;
import rental.strategy.HourlyPricing;
import rental.strategy.PricingStrategy;

/**
 * Pattern: FACADE. Role: Facade.
 *
 * The client (Main) talks only to this class. Behind it there are the other parts of the system:
 * factories (create vehicles), the fleet (stores them), strategies (price), decorators (extras)
 * and observers (notifications). The request comes from the Builder. The facade knows in which
 * order to call everything, the client does not.
 */
public class RentalFacade {

    private final FleetRepository fleet = new FleetRepository();
    private final Map<VehicleType, VehicleFactory> factories = new EnumMap<>(VehicleType.class);
    private final Map<PricingType, PricingStrategy> strategies = new EnumMap<>(PricingType.class);
    private final Map<Integer, Rental> rentals = new LinkedHashMap<>();
    private int nextRentalId = 1;

    public RentalFacade() {
        factories.put(VehicleType.CAR, new CarFactory());
        factories.put(VehicleType.BIKE, new BikeFactory());
        factories.put(VehicleType.VAN, new VanFactory());
        strategies.put(PricingType.HOURLY, new HourlyPricing());
        strategies.put(PricingType.DAILY, new DailyPricing());
    }

    // ---------- fleet ----------

    public Vehicle addVehicle(VehicleType type, String model) {
        Vehicle vehicle = factories.get(type).create(model);
        fleet.add(vehicle);
        return vehicle;
    }

    public Vehicle getVehicle(String vehicleId) {
        return fleet.findById(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return fleet.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return fleet.findAvailable();
    }

    // ---------- renting ----------

    /** Calculates the price without renting anything. */
    public RentalCharge quote(RentalRequest request) {
        Vehicle vehicle = fleet.findById(request.getVehicleId());
        PricingStrategy strategy = strategies.get(request.getPricingType());
        RentalCharge charge = new BaseRentalCharge(vehicle, strategy, request.getUnits());
        for (Extra extra : request.getExtras()) {
            charge = extra.wrap(charge);
        }
        return charge;
    }

    public Rental rent(RentalRequest request) {
        RentalCharge charge = quote(request);
        Vehicle vehicle = fleet.findById(request.getVehicleId());
        vehicle.markRented(); // throws if somebody else already has this vehicle

        Rental rental = new Rental(nextRentalId++, request.getCustomer(), vehicle, charge);
        rentals.put(rental.getId(), rental);
        return rental;
    }

    /** Closes the rental. The vehicle notifies its observers inside markReturned(). */
    public Rental returnVehicle(int rentalId) {
        Rental rental = rentals.get(rentalId);
        if (rental == null) {
            throw new RentalNotFoundException(rentalId);
        }
        if (!rental.isActive()) {
            throw new IllegalStateException("Rental #" + rentalId + " is already closed");
        }
        rental.close();
        rental.getVehicle().markReturned();
        return rental;
    }

    public List<Rental> getActiveRentals() {
        List<Rental> active = new ArrayList<>();
        for (Rental rental : rentals.values()) {
            if (rental.isActive()) {
                active.add(rental);
            }
        }
        return active;
    }

    // ---------- notifications ----------

    public void subscribe(String vehicleId, AvailabilityObserver observer) {
        fleet.findById(vehicleId).subscribe(observer);
    }

    public void unsubscribe(String vehicleId, AvailabilityObserver observer) {
        fleet.findById(vehicleId).unsubscribe(observer);
    }
}
