import java.util.ArrayList;
import java.util.Scanner;

public class RentalApp {

    private static ArrayList<Vehicle> vehicles = new ArrayList<>();
    private static double totalIncome = 0;

    private static final int CMD_WIDTH = 120;

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        bigHeading("Vehicle Rental System");
//        printBlockCentered(TextArt::showBanner);
        gap(2);

        totalIncome = FileManager.load(vehicles);

        int choice;
        do {
            sectionHeading("Main Menu");

            String[] menu = {
                    "1. Add Vehicle",
                    "2. View All Vehicles",
                    "3. Rent a Vehicle",
                    "4. Return a Vehicle",
                    "5. Search Vehicle by ID",
                    "6. View Total Rental Income",
                    "7. Exit"
            };

            drawBoxCentered(menu);
            gap(1);

            text("Enter your choice: ");
            try {
                choice = Integer.parseInt(sc.nextLine());
                gap(2);

                switch (choice) {
                    case 1 -> addVehicle(sc);
                    case 2 -> viewVehicles();
                    case 3 -> rentVehicle(sc);
                    case 4 -> returnVehicle(sc);
                    case 5 -> searchVehicle(sc);
                    case 6 -> text("Total Rental Income : Rs. " + YELLOW + totalIncome + RESET);
                    case 7 -> {
                        FileManager.save(vehicles, totalIncome);
                        text("Exiting and saving data...");
                    }
                    default -> text("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                text("Invalid input! Please enter a number.");
                choice = 0;
            }

            gap(2);

        } while (choice != 7);

        sc.close();
    }



    private static void printlnC(String text) {
        int pad = (CMD_WIDTH - stripAnsi(text).length()) / 2;
        if (pad < 0) pad = 0;
        System.out.println(" ".repeat(pad) + text);
    }

    private static void printBlockCentered(Runnable block) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(baos));

        block.run();

        System.out.flush();
        System.setOut(old);

        for (String line : baos.toString().split("\n")) {
            printlnC(line);
        }
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    //  STYLING

    private static void bigHeading(String text) {
        String line = "=".repeat(text.length() + 12);
        printlnC(CYAN + line + RESET);
        printlnC(CYAN + "===   " + BOLD + text.toUpperCase() + RESET + CYAN + "   ===" + RESET);
        printlnC(CYAN + line + RESET);
        gap(2);
    }

    private static void sectionHeading(String text) {
        printlnC(PURPLE + BOLD + ">> " + text + " <<" + RESET);
        gap(1);
    }

    private static void text(String text) {
        printlnC(text);
    }

    private static void gap(int lines) {
        for (int i = 0; i < lines; i++) System.out.println();
    }

    // BOX

    private static void drawBoxCentered(String[] lines) {
        int width = 0;
        for (String l : lines)
            width = Math.max(width, stripAnsi(l).length());

        int boxWidth = width + 4;
        int pad = (CMD_WIDTH - boxWidth) / 2;
        if (pad < 0) pad = 0;

        String p = " ".repeat(pad);

        System.out.println(p + "┌" + "─".repeat(width + 2) + "┐");
        for (String l : lines) {
            int space = width - stripAnsi(l).length();
            System.out.println(p + "│ " + l + " ".repeat(space) + " │");
        }
        System.out.println(p + "└" + "─".repeat(width + 2) + "┘");
    }

    // PROGRESS BAR

    private static void showProgressBar(String message, int seconds) {
        int barLength = 20;
        int totalSteps = seconds * 20;

        for (int i = 0; i <= totalSteps; i++) {
            int filled = (i * barLength) / totalSteps;
            int percent = (i * 100) / totalSteps;

            String bar = "[" +
                    "#".repeat(filled) +
                    "-".repeat(barLength - filled) +
                    "] " + percent + "%";

            String text = message + " " + bar;

            int pad = (CMD_WIDTH - stripAnsi(text).length()) / 2;
            if (pad < 0) pad = 0;

            System.out.print("\r" + " ".repeat(pad) + text);

            try {
                Thread.sleep(1000 / 20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }



    private static void addVehicle(Scanner sc) {
        sectionHeading("Add New Vehicle");

        text("Enter type (Car/Bike/Van): ");
        String type = sc.nextLine();

        text("Enter Vehicle ID: ");
        String id = sc.nextLine();

        if (searchById(id) != null) {
            text("Vehicle ID already exists!");
            return;
        }

        text("Enter Brand: ");
        String brand = sc.nextLine();

        text("Enter Model: ");
        String model = sc.nextLine();

        text("Enter Base Rate: ");
        double rate = Double.parseDouble(sc.nextLine());

        Vehicle v = switch (type.toLowerCase()) {
            case "car" -> {
                text("Enter number of seats: ");
                yield new Car(id, brand, model, rate, Integer.parseInt(sc.nextLine()));
            }
            case "bike" -> {
                text("Enter engine capacity CC: ");
                yield new Bike(id, brand, model, rate, Integer.parseInt(sc.nextLine()));
            }
            case "van" -> {
                text("Enter cargo capacity kg: ");
                yield new Van(id, brand, model, rate, Double.parseDouble(sc.nextLine()));
            }
            default -> null;
        };
            //process bar
        if (v != null) {
            showProgressBar("Adding vehicle", 3);

            vehicles.add(v);
            FileManager.save(vehicles, totalIncome);

            gap(1);
            text(GREEN + "Vehicle added successfully!" + RESET);
        }
    }

    private static void viewVehicles() {
        if (vehicles.isEmpty()) {
            text("No vehicles available.");
            return;
        }

        bigHeading("Vehicle List");

        printBlockCentered(() -> {
            System.out.println(YELLOW + "================ Vehicle List ================" + RESET);
            System.out.printf("%-10s %-12s %-12s %-10s %-10s\n",
                    "ID", "Brand", "Model", "Rate", "Available");
            System.out.println("----------------------------------------------------------");

            for (Vehicle v : vehicles) {
                System.out.printf("%-10s %-12s %-12s %-10.2f %-10s\n",
                        v.getVehicleId(),
                        v.getBrand(),
                        v.getModel(),
                        v.getBaseRatePerDay(),
                        v.isAvailable() ? GREEN + "Yes" + RESET : RED + "No" + RESET
                );
            }
        });
    }

    private static void rentVehicle(Scanner sc) {
        sectionHeading("Rent Vehicle");

        text("Enter Vehicle ID to rent: ");
        String id = sc.nextLine();

        Vehicle v = searchById(id);
        if (v == null || !v.isAvailable()) {
            text(RED + "Vehicle not available!" + RESET);
            return;
        }

        text("Enter number of rental days: ");
        int days = Integer.parseInt(sc.nextLine());

        v.rentVehicle();
        double cost = v.calculateRentalCost(days);
        totalIncome += cost;

        FileManager.save(vehicles, totalIncome);
        text("Rental cost : Rs. " + YELLOW + cost + RESET);
    }

    private static void returnVehicle(Scanner sc) {
        sectionHeading("Return Vehicle");

        text("Enter Vehicle ID to return: ");
        String id = sc.nextLine();

        Vehicle v = searchById(id);
        if (v == null) {
            text("Vehicle not found!");
            return;
        }

        v.returnVehicle();
        FileManager.save(vehicles, totalIncome);
        text(GREEN + "Vehicle returned successfully!" + RESET);
    }

    private static void searchVehicle(Scanner sc) {
        sectionHeading("Search Vehicle");

        text("Enter Vehicle ID to search: ");
        String id = sc.nextLine();
        gap(1);

        Vehicle v = searchById(id);
        if (v == null) {
            text("Vehicle not found!");
        } else {
            printBlockCentered(v::displayDetails);
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
