import java.util.ArrayList;
import java.util.Scanner;

public class RentalApp {
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static double totalIncome = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TextArt.showBanner();
        totalIncome = FileManager.load(vehicles);

        int choice;
        do {
            System.out.println("\n=== Vehicle Rental System ===");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Rent a Vehicle");
            System.out.println("4. Return a Vehicle");
            System.out.println("5. Search Vehicle by ID");
            System.out.println("6. View Total Rental Income");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");


            try {
                choice = Integer.parseInt(sc.nextLine());

                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");


                switch (choice) {
                    case 1 -> addVehicle(sc);
                    case 2 -> viewVehicles();
                    case 3 -> rentVehicle(sc);
                    case 4 -> returnVehicle(sc);
                    case 5 -> searchVehicle(sc);
                    case 6 -> System.out.println("Total Rental Income: " + totalIncome);
                    case 7 -> {
                        // Save data before exit
                        FileManager.save(vehicles, totalIncome);
                        System.out.println("Exiting and saving data...");
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                choice = 0;
            }

        } while (choice != 7);


        sc.close();
    }

    private static void addVehicle(Scanner sc) {
        System.out.print("Enter type (Car/Bike/Van): ");
        String type = sc.nextLine();
        System.out.print("Enter Vehicle ID: "); String id = sc.nextLine();

        if (searchById(id) != null) {
            System.out.println("Vehicle ID already exists!");
            return;
        }

        System.out.print("Enter Brand: "); String brand = sc.nextLine();
        System.out.print("Enter Model: "); String model = sc.nextLine();
        System.out.print("Enter Base Rate: "); double rate = Double.parseDouble(sc.nextLine());

        Vehicle v = switch (type.toLowerCase()) {
            case "car" -> {
                System.out.print("Enter number of seats: "); int seats = Integer.parseInt(sc.nextLine());
                yield new Car(id, brand, model, rate, seats);
            }
            case "bike" -> {
                System.out.print("Enter engine capacity CC: "); int cc = Integer.parseInt(sc.nextLine());
                yield new Bike(id, brand, model, rate, cc);
            }
            case "van" -> {
                System.out.print("Enter cargo capacity kg: "); double cargo = Double.parseDouble(sc.nextLine());
                yield new Van(id, brand, model, rate, cargo);
            }
            default -> {
                System.out.println("Unknown vehicle type!");
                yield null;
            }
        };

        if (v != null) {
            vehicles.add(v);
            System.out.println("Vehicle added successfully!");
            FileManager.save(vehicles, totalIncome);
        }

    }

    private static void viewVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles available.");
            return;
        }

        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String BLUE = "\u001B[34m";
        final String CYAN = "\u001B[36m";
        final String YELLOW = "\u001B[33m";

        System.out.println(YELLOW+"\n==================="+RESET+CYAN+"Vehicle List"+RESET+YELLOW+"==================="+RESET);
        System.out.println(" ");
        System.out.printf("%-10s %-12s %-12s %-10s %-10s\n", "ID", "Brand", "Model", "Rate", "Available");
        System.out.println("----------------------------------------------------------");

        for (Vehicle v : vehicles) {
            System.out.printf("%-10s %-12s %-12s %-10.2f %-10s\n",
                    v.getVehicleId(),
                    v.getBrand(),
                    v.getModel(),
                    v.getBaseRatePerDay(),
                    v.isAvailable() ? GREEN+"Yes"+RESET : RED+"No"+RESET
            );
        }
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
    }

    private static void rentVehicle(Scanner sc) {
        System.out.print("Enter Vehicle ID to rent: ");
        String id = sc.nextLine();
        Vehicle v = searchById(id);
        if (v == null) {
            System.out.println("Vehicle not found!");
            return;
        }
        if (!v.isAvailable()) {
            System.out.println("Vehicle is already rented!");
            return;
        }

        System.out.print("Enter number of rental days: ");
        int days = Integer.parseInt(sc.nextLine());
        if (days <= 0) {
            System.out.println("Invalid number of days!");
            return;
        }

        v.rentVehicle();
        totalIncome += v.calculateRentalCost(days);
        System.out.println("Rental cost: " + v.calculateRentalCost(days));

        // Save after renting
        FileManager.save(vehicles, totalIncome);
    }

    private static void returnVehicle(Scanner sc) {
        System.out.print("Enter Vehicle ID to return: ");
        String id = sc.nextLine();
        Vehicle v = searchById(id);
        if (v == null) {
            System.out.println("Vehicle not found!");
            return;
        }

        v.returnVehicle();
        // Save after returning
        FileManager.save(vehicles, totalIncome);
    }

    private static void searchVehicle(Scanner sc) {
        System.out.print("Enter Vehicle ID to search: ");
        String id = sc.nextLine();
        Vehicle v = searchById(id);
        if (v == null) {
            System.out.println("Vehicle not found!");
        } else {
            v.displayDetails();
        }
    }

    private static Vehicle searchById(String id) {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equalsIgnoreCase(id)) {
                return v;
            }
        }
        return null;
    }
}
