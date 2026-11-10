# Vehicle Rental Service

Final team project for the course **Software Design Patterns** (Astana IT University, group SE 2502).
Authors: Shyndaly Bagnur, Aruzhan Shamshidin.

A console application for a vehicle rental company: customers rent cars, bikes and vans per hour or per day,
choose extras (GPS, child seat, insurance) and can subscribe to a notification when a rented vehicle is returned.

## Patterns

| Pattern | Problem in the project | Main classes | Implemented by |
|---|---|---|---|
| Factory Method | create Car / Bike / Van without `new` in the client | `VehicleFactory`, `CarFactory`, `BikeFactory`, `VanFactory` | Shyndaly |
| Builder | build a rental request step by step | `RentalRequestBuilder`, `RentalRequest` | Shyndaly |
| Strategy | price per hour or per day | `PricingStrategy`, `HourlyPricing`, `DailyPricing` | Shyndaly |
| Decorator | any combination of extras on top of the price | `RentalCharge`, `RentalChargeDecorator`, `GpsDecorator`, `ChildSeatDecorator`, `InsuranceDecorator`, `Extra` | Aruzhan |
| Observer | notify waiting customers when a vehicle is returned | `AvailabilitySubject`, `AvailabilityObserver`, `Vehicle`, `CustomerNotifier`, `AvailabilityLogger` | Aruzhan |
| Facade | one entry point to all subsystems | `RentalFacade` | Aruzhan |

## Run (JDK 17 or newer)

Windows: `run.bat` (demo) and `test.bat` (tests). Linux / macOS / Git Bash: `./run.sh` and `./test.sh`.

```
javac -d out $(find src -name "*.java")
java -cp out rental.Main
java -cp out rental.RentalTests
```

## UML

PlantUML sources are in `docs/uml` (00 overview, 01-06 one per pattern, 07 sequence).
Open them in VS Code with the PlantUML extension, or paste the text on https://www.plantuml.com/plantuml.
