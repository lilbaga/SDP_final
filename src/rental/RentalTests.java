package rental;

import java.util.EnumSet;
import rental.builder.RentalRequest;
import rental.builder.RentalRequestBuilder;
import rental.decorator.BaseRentalCharge;
import rental.decorator.Extra;
import rental.decorator.GpsDecorator;
import rental.decorator.InsuranceDecorator;
import rental.decorator.RentalCharge;
import rental.exception.RentalNotFoundException;
import rental.exception.VehicleNotAvailableException;
import rental.exception.VehicleNotFoundException;
import rental.facade.RentalFacade;
import rental.model.Bike;
import rental.model.Car;
import rental.model.Customer;
import rental.model.PricingType;
import rental.model.Rental;
import rental.model.Van;
import rental.model.Vehicle;
import rental.model.VehicleType;
import rental.observer.AvailabilityLogger;
import rental.observer.CustomerNotifier;
import rental.strategy.DailyPricing;
import rental.strategy.HourlyPricing;

/**
 * Plain Java tests without any library. Run: java -cp out rental.RentalTests
 * A failed check throws AssertionError, a passed check prints a PASS line.
 * Every test creates its own facade with its own empty fleet (F.I.R.S.T.: tests are independent).
 */
public class RentalTests {

    private static final double DELTA = 0.0001;
    private static final Customer ARUZHAN = new Customer("Aruzhan");

    public static void main(String[] args) {
        testFactoryCreatesCorrectTypesAndIds();
        testBuilderBuildsRequest();
        testBuilderRejectsWrongRequests();
        testHourlyStrategy();
        testDailyStrategyWithDiscount();
        testDecoratorsAddUp();
        testDecoratorOrderMatters();
        testObserverIsNotifiedOnReturn();
        testUnsubscribedObserverIsNotNotified();
        testFacadeFullRentalFlow();
        testFacadeErrors();
        System.out.println("ALL TESTS PASSED");
    }

    private static RentalRequest dailyRequest(String vehicleId, int days, Extra... extras) {
        RentalRequestBuilder builder = new RentalRequestBuilder().forCustomer(ARUZHAN).vehicle(vehicleId).daily(days);
        for (Extra extra : extras) {
            builder.withExtra(extra);
        }
        return builder.build();
    }

    private static void testFactoryCreatesCorrectTypesAndIds() {
        RentalFacade facade = new RentalFacade();
        Vehicle car = facade.addVehicle(VehicleType.CAR, "Camry");
        Vehicle bike = facade.addVehicle(VehicleType.BIKE, "Trek");
        Vehicle van = facade.addVehicle(VehicleType.VAN, "Transit");
        Vehicle secondCar = facade.addVehicle(VehicleType.CAR, "Elantra");

        assertTrue(car instanceof Car && bike instanceof Bike && van instanceof Van, "factory: right classes");
        assertEquals("CAR-001", car.getId(), "factory: first car id");
        assertEquals("CAR-002", secondCar.getId(), "factory: second car id");
        assertEquals("BIKE-001", bike.getId(), "factory: first bike id");
    }

    private static void testBuilderBuildsRequest() {
        RentalRequest request = new RentalRequestBuilder()
                .forCustomer(ARUZHAN).vehicle("CAR-001").hourly(5)
                .withExtra(Extra.INSURANCE).withExtra(Extra.GPS).build();

        assertEquals("CAR-001", request.getVehicleId(), "builder: vehicle id");
        assertEquals(5, request.getUnits(), "builder: duration");
        assertTrue(request.getPricingType() == PricingType.HOURLY, "builder: hourly pricing type");
        assertTrue(request.getExtras().iterator().next() == Extra.GPS, "builder: extras keep the enum order");
    }

    private static void testBuilderRejectsWrongRequests() {
        assertThrows(IllegalStateException.class,
                () -> new RentalRequestBuilder().forCustomer(ARUZHAN).daily(1).build(), "builder: no vehicle");
        assertThrows(IllegalStateException.class,
                () -> new RentalRequestBuilder().vehicle("CAR-001").daily(1).build(), "builder: no customer");
        assertThrows(IllegalStateException.class,
                () -> new RentalRequestBuilder().forCustomer(ARUZHAN).vehicle("CAR-001").build(),
                "builder: no duration");
        assertThrows(IllegalArgumentException.class,
                () -> new RentalRequestBuilder().daily(0), "builder: zero days");
    }

    private static void testHourlyStrategy() {
        Vehicle car = new RentalFacade().addVehicle(VehicleType.CAR, "Camry");
        assertEquals(40.0, new HourlyPricing().calculatePrice(car, 5), "strategy: 5 hours of a car");
    }

    private static void testDailyStrategyWithDiscount() {
        RentalFacade facade = new RentalFacade();
        Vehicle car = facade.addVehicle(VehicleType.CAR, "Camry");
        Vehicle van = facade.addVehicle(VehicleType.VAN, "Transit");
        assertEquals(150.0, new DailyPricing().calculatePrice(car, 3), "strategy: 3 days of a car");
        assertEquals(480.0, new DailyPricing().calculatePrice(van, 6), "strategy: 6 days of a van, no discount");
        assertEquals(504.0, new DailyPricing().calculatePrice(van, 7), "strategy: 7 days of a van, 10% discount");
    }

    private static void testDecoratorsAddUp() {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Camry");
        RentalCharge charge = facade.quote(dailyRequest("CAR-001", 3, Extra.GPS, Extra.CHILD_SEAT, Extra.INSURANCE));
        assertEquals(181.7, charge.getCost(), "decorator: (150 + 5 + 3) * 1.15");
        assertTrue(charge.getDescription().endsWith("+ GPS + child seat + insurance"), "decorator: description");
    }

    private static void testDecoratorOrderMatters() {
        Vehicle car = new RentalFacade().addVehicle(VehicleType.CAR, "Camry");
        RentalCharge base = new BaseRentalCharge(car, new DailyPricing(), 3);
        RentalCharge insuranceLast = new InsuranceDecorator(new GpsDecorator(base));
        RentalCharge insuranceFirst = new GpsDecorator(new InsuranceDecorator(base));
        assertEquals(178.25, insuranceLast.getCost(), "decorator: insurance last = (150 + 5) * 1.15");
        assertEquals(177.5, insuranceFirst.getCost(), "decorator: insurance first = 150 * 1.15 + 5");
    }

    private static void testObserverIsNotifiedOnReturn() {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Camry");
        CustomerNotifier notifier = new CustomerNotifier(new Customer("Shyndaly"));
        AvailabilityLogger logger = new AvailabilityLogger();

        Rental rental = facade.rent(dailyRequest("CAR-001", 1));
        facade.subscribe("CAR-001", notifier);
        facade.subscribe("CAR-001", logger);
        facade.returnVehicle(rental.getId());

        assertEquals(1, notifier.getInbox().size(), "observer: customer got one message");
        assertEquals(1, logger.getEventCount(), "observer: logger got one event");
    }

    private static void testUnsubscribedObserverIsNotNotified() {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.BIKE, "Trek");
        CustomerNotifier notifier = new CustomerNotifier(new Customer("Shyndaly"));

        Rental rental = facade.rent(dailyRequest("BIKE-001", 1));
        facade.subscribe("BIKE-001", notifier);
        facade.unsubscribe("BIKE-001", notifier);
        facade.returnVehicle(rental.getId());

        assertEquals(0, notifier.getInbox().size(), "observer: unsubscribed customer gets nothing");
    }

    private static void testFacadeFullRentalFlow() {
        RentalFacade facade = new RentalFacade();
        Vehicle van = facade.addVehicle(VehicleType.VAN, "Transit");

        RentalRequest request = new RentalRequestBuilder()
                .forCustomer(ARUZHAN).vehicle("VAN-001").hourly(4).withExtra(Extra.INSURANCE).build();
        Rental rental = facade.rent(request);
        assertEquals(55.2, rental.getCharge().getCost(), "facade: 4 hours of a van with insurance");
        assertTrue(!van.isAvailable(), "facade: vehicle is rented");
        assertEquals(1, facade.getActiveRentals().size(), "facade: one active rental");

        facade.returnVehicle(rental.getId());
        assertTrue(van.isAvailable(), "facade: vehicle is available after return");
        assertEquals(0, facade.getActiveRentals().size(), "facade: no active rentals");
    }

    private static void testFacadeErrors() {
        RentalFacade facade = new RentalFacade();
        facade.addVehicle(VehicleType.CAR, "Camry");
        facade.rent(dailyRequest("CAR-001", 1));

        assertThrows(VehicleNotAvailableException.class,
                () -> facade.rent(dailyRequest("CAR-001", 1)), "errors: vehicle already rented");
        assertThrows(VehicleNotFoundException.class,
                () -> facade.rent(dailyRequest("BUS-999", 1)), "errors: unknown vehicle");
        assertThrows(RentalNotFoundException.class,
                () -> facade.returnVehicle(99), "errors: unknown rental");
    }

    // ------------------------------------------------------------------ tiny assert helpers
    private static void assertTrue(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError(name);
        }
        System.out.println("PASS: " + name);
    }

    private static void assertEquals(Object expected, Object actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": expected " + expected + " but was " + actual);
        }
        System.out.println("PASS: " + name);
    }

    private static void assertEquals(double expected, double actual, String name) {
        if (Math.abs(expected - actual) > DELTA) {
            throw new AssertionError(name + ": expected " + expected + " but was " + actual);
        }
        System.out.println("PASS: " + name);
    }

    private static void assertThrows(Class<? extends RuntimeException> type, Runnable task, String name) {
        try {
            task.run();
        } catch (RuntimeException e) {
            if (type.isInstance(e)) {
                System.out.println("PASS: " + name);
                return;
            }
            throw new AssertionError(name + ": expected " + type.getSimpleName() + " but got " + e);
        }
        throw new AssertionError(name + ": expected " + type.getSimpleName() + " but nothing was thrown");
    }
}
