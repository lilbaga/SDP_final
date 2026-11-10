package rental;

import java.util.List;
import rental.builder.RentalRequest;
import rental.builder.RentalRequestBuilder;
import rental.decorator.BaseRentalCharge;
import rental.decorator.ChildSeatDecorator;
import rental.decorator.Extra;
import rental.decorator.GpsDecorator;
import rental.decorator.InsuranceDecorator;
import rental.decorator.RentalCharge;
import rental.exception.VehicleNotAvailableException;
import rental.facade.RentalFacade;
import rental.model.Customer;
import rental.model.Rental;
import rental.model.Vehicle;
import rental.model.VehicleType;
import rental.observer.AvailabilityLogger;
import rental.observer.CustomerNotifier;
import rental.strategy.DailyPricing;
import rental.util.Money;

/**
 * The client of the system. It runs a demo in six scenes, one scene for every design pattern.
 * Run it and take the screenshots of the application from this output.
 */
public class Main {

    private static final String LINE = "=".repeat(78);

    public static void main(String[] args) {
        RentalFacade facade = new RentalFacade();
        Customer aruzhan = new Customer("Aruzhan");
        Customer shyndaly = new Customer("Shyndaly");

        System.out.println(LINE);
        System.out.println("   VEHICLE RENTAL SERVICE - Software Design Patterns, SE 2502");
        System.out.println(LINE);

        demoFactoryMethod(facade);
        demoBuilder(aruzhan);
        demoStrategy(facade, aruzhan);
        demoDecorator(facade);
        demoObserver(facade, aruzhan, shyndaly);
        demoFacade(facade, aruzhan);
    }

    // ------------------------------------------------------------------ 1. Factory Method
    private static void demoFactoryMethod(RentalFacade facade) {
        printHeader("1. FACTORY METHOD - factories create the vehicles");
        facade.addVehicle(VehicleType.CAR, "Toyota Camry");
        facade.addVehicle(VehicleType.CAR, "Hyundai Elantra");
        facade.addVehicle(VehicleType.BIKE, "Trek FX 3");
        facade.addVehicle(VehicleType.BIKE, "Giant Escape");
        facade.addVehicle(VehicleType.VAN, "Ford Transit");
        facade.addVehicle(VehicleType.VAN, "Mercedes Sprinter");
        printFleet(facade.getAllVehicles());
    }

    // ------------------------------------------------------------------ 2. Builder
    private static void demoBuilder(Customer aruzhan) {
        printHeader("2. BUILDER - a rental request is built step by step");
        RentalRequest request = new RentalRequestBuilder() //Main.java
                .forCustomer(aruzhan)
                .vehicle("CAR-001")
                .daily(3)
                .withExtra(Extra.INSURANCE)
                .withExtra(Extra.GPS)
                .build();
        System.out.println("   Built request: " + request);
        System.out.println("   (INSURANCE was added first, but the extras are kept in the enum order)");

        System.out.println("   Incomplete requests are rejected:");
        tryRun("no vehicle    ", () -> new RentalRequestBuilder().forCustomer(aruzhan).daily(2).build());
        tryRun("no customer   ", () -> new RentalRequestBuilder().vehicle("CAR-001").daily(2).build());
        tryRun("0 days        ", () -> new RentalRequestBuilder().forCustomer(aruzhan).vehicle("CAR-001").daily(0));
    }

    // ------------------------------------------------------------------ 3. Strategy
    private static void demoStrategy(RentalFacade facade, Customer customer) {
        printHeader("3. STRATEGY - the price algorithm can be exchanged");
        printQuote(facade, hourly(customer, "CAR-001", 5));
        printQuote(facade, daily(customer, "CAR-001", 3));
        printQuote(facade, daily(customer, "VAN-001", 6));
        printQuote(facade, daily(customer, "VAN-001", 7));
        System.out.println("   (7 days and more get a 10% discount in DailyPricing)");
    }

    // ------------------------------------------------------------------ 4. Decorator
    private static void demoDecorator(RentalFacade facade) {
        printHeader("4. DECORATOR - extras are wrapped around the basic price");
        Vehicle car = facade.getVehicle("CAR-001");

        RentalCharge charge = new BaseRentalCharge(car, new DailyPricing(), 3);
        printLayer("Base price           ", charge);
        charge = new GpsDecorator(charge);
        printLayer("+ GPS                ", charge);
        charge = new ChildSeatDecorator(charge);
        printLayer("+ child seat         ", charge);
        charge = new InsuranceDecorator(charge);
        printLayer("+ insurance (last)   ", charge);

        RentalCharge insuranceFirst = new InsuranceDecorator(new BaseRentalCharge(car, new DailyPricing(), 3));
        insuranceFirst = new ChildSeatDecorator(new GpsDecorator(insuranceFirst));
        System.out.println("   Same extras, insurance wrapped FIRST: " + Money.format(insuranceFirst.getCost()));
        System.out.println("   -> the order of decorators changes the price");
    }

    // ------------------------------------------------------------------ 5. Observer
    private static void demoObserver(RentalFacade facade, Customer aruzhan, Customer shyndaly) {
        printHeader("5. OBSERVER - customers are notified when a vehicle is returned");
        Rental first = facade.rent(daily(aruzhan, "CAR-001", 2, Extra.GPS));
        System.out.println("   " + first.getSummary());

        try {
            facade.rent(hourly(shyndaly, "CAR-001", 3));
        } catch (VehicleNotAvailableException e) {
            System.out.println("   Shyndaly tried to rent CAR-001 -> " + e.getMessage());
        }

        CustomerNotifier notifier = new CustomerNotifier(shyndaly);
        AvailabilityLogger logger = new AvailabilityLogger();
        facade.subscribe("CAR-001", notifier);
        facade.subscribe("CAR-001", logger);
        System.out.println("   Shyndaly and the fleet manager subscribed to CAR-001");

        System.out.println("   Aruzhan returns CAR-001 ...");
        facade.returnVehicle(first.getId());

        Rental second = facade.rent(hourly(shyndaly, "CAR-001", 3));
        facade.unsubscribe("CAR-001", notifier);
        System.out.println("   " + second.getSummary());
        System.out.println("   Shyndaly has the car now and unsubscribed from CAR-001");
    }

    // ------------------------------------------------------------------ 6. Facade
    private static void demoFacade(RentalFacade facade, Customer aruzhan) {
        printHeader("6. FACADE - the client calls one class, the facade does the rest");
        Rental van = facade.rent(hourly(aruzhan, "VAN-001", 4, Extra.INSURANCE));
        System.out.println("   " + van.getSummary());
        facade.returnVehicle(van.getId());
        System.out.println("   Rental #" + van.getId() + " closed, VAN-001 is free again");

        System.out.println("   Error handling:");
        tryRun("rent unknown vehicle BUS-999", () -> facade.rent(daily(aruzhan, "BUS-999", 1)));
        tryRun("return unknown rental #99 ", () -> facade.returnVehicle(99));

        System.out.println("   Active rentals:");
        for (Rental rental : facade.getActiveRentals()) {
            System.out.println("     " + rental.getSummary());
        }
        System.out.println("   Available vehicles: " + facade.getAvailableVehicles().size()
                + " of " + facade.getAllVehicles().size());
        System.out.println(LINE);
    }

    // ------------------------------------------------------------------ helpers
    private static RentalRequest daily(Customer customer, String vehicleId, int days, Extra... extras) {
        RentalRequestBuilder builder = new RentalRequestBuilder().forCustomer(customer).vehicle(vehicleId).daily(days);
        for (Extra extra : extras) {
            builder.withExtra(extra);
        }
        return builder.build();
    }

    private static RentalRequest hourly(Customer customer, String vehicleId, int hours, Extra... extras) {
        RentalRequestBuilder builder = new RentalRequestBuilder().forCustomer(customer).vehicle(vehicleId).hourly(hours);
        for (Extra extra : extras) {
            builder.withExtra(extra);
        }
        return builder.build();
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    private static void printFleet(List<Vehicle> vehicles) {
        System.out.printf("   %-9s %-6s %-18s %-16s %9s %9s %s%n",
                "ID", "Type", "Model", "Details", "Per hour", "Per day", "Status");
        for (Vehicle vehicle : vehicles) {
            System.out.printf("   %-9s %-6s %-18s %-16s %9.2f %9.2f %s%n",
                    vehicle.getId(), vehicle.getType().getLabel(), vehicle.getModel(), vehicle.getDetails(),
                    vehicle.getHourlyRate(), vehicle.getDailyRate(),
                    vehicle.isAvailable() ? "available" : "rented");
        }
    }

    private static void printQuote(RentalFacade facade, RentalRequest request) {
        RentalCharge charge = facade.quote(request);
        System.out.println("   " + charge.getDescription() + " -> " + Money.format(charge.getCost()));
    }

    private static void printLayer(String label, RentalCharge charge) {
        System.out.println("   " + label + " = " + Money.format(charge.getCost()));
    }

    private static void tryRun(String action, Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            System.out.println("     " + action + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
