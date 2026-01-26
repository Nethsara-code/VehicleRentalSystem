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

            String choiceStr = inputCentered("Enter your choice: ", sc);

            try {
                choice = Integer.parseInt(choiceStr);
                gap(2);

                switch (choice) {
                    case 1 -> openScene("Add Vehicle", () -> addVehicleInner(sc));
                    case 2 -> openScene("Vehicle List", RentalApp::viewVehicles);
                    case 3 -> openScene("Rent Vehicle", () -> rentVehicle(sc));
                    case 4 -> openScene("Return Vehicle", () -> returnVehicle(sc));
                    case 5 -> openScene("Search Vehicle", () -> searchVehicle(sc));
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

    //  NEW SCENE

    private static void openScene(String title, Runnable content) {
        clearScreen();
        bigHeading(title);
        content.run();
        gap(2);
        printlnC("Press ENTER to return to Main Menu...");
        new Scanner(System.in).nextLine();
        clearScreen();
    }

    private static void clearScreen() {

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //  CENTER HELPERS

    private static void printlnC(String text) {
        int pad = (CMD_WIDTH - stripAnsi(text).length()) / 2;
        if (pad < 0) pad = 0;
        System.out.println(" ".repeat(pad) + text);
    }

    private static String inputCentered(String prompt, Scanner sc) {
        int pad = (CMD_WIDTH - stripAnsi(prompt).length()) / 2;
        if (pad < 0) pad = 0;

        System.out.print(" ".repeat(pad) + prompt);
        return sc.nextLine();
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private static void gap(int lines) {
        for (int i = 0; i < lines; i++) System.out.println();
    }

    //  STYLING

    private static void bigHeading(String text) {
        String line = "=".repeat(text.length() + 12);
        printlnC(CYAN + line + RESET);
        printlnC(CYAN + "===   " + BOLD + text.toUpperCase() + RESET + CYAN + "   ===" + RESET);
        printlnC(CYAN + line + RESET);
    }

    private static void sectionHeading(String text) {
        printlnC(PURPLE + BOLD + ">> " + text + " <<" + RESET);
        gap(1);
    }

    private static void text(String text) {
        printlnC(text);
    }

    //  BOX

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

    //  PROGRESS BAR

    private static void showProgressBar(String message, int seconds) {
        int barLength = 20;
        int totalSteps = seconds * 20;

        for (int i = 0; i <= totalSteps; i++) {
            int filled = (i * barLength) / totalSteps;
            int percent = (i * 100) / totalSteps;

            String bar = "[" + "#".repeat(filled) + "-".repeat(barLength - filled) + "] " + percent + "%";
            String text = message + " " + bar;

            int pad = (CMD_WIDTH - stripAnsi(text).length()) / 2;
            if (pad < 0) pad = 0;

            System.out.print("\r" + " ".repeat(pad) + text);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println();
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ADD VEHICLE

    private static void addVehicleInner(Scanner sc) {
        sectionHeading("Add New Vehicle");

        String type = inputCentered("Enter type (Car/Bike/Van): ", sc);
        String id = inputCentered("Enter Vehicle ID: ", sc);

        if (searchById(id) != null) {
            text("Vehicle ID already exists!");
            return;
        }

        String brand = inputCentered("Enter Brand: ", sc);
        String model = inputCentered("Enter Model: ", sc);
        double rate = Double.parseDouble(inputCentered("Enter Base Rate: ", sc));

        Vehicle v = switch (type.toLowerCase()) {
            case "car" -> new Car(id, brand, model, rate,
                    Integer.parseInt(inputCentered("Enter number of seats: ", sc)));
            case "bike" -> new Bike(id, brand, model, rate,
                    Integer.parseInt(inputCentered("Enter engine capacity CC: ", sc)));
            case "van" -> new Van(id, brand, model, rate,
                    Double.parseDouble(inputCentered("Enter cargo capacity kg: ", sc)));
            default -> null;
        };

        if (v != null) {
            showProgressBar("Adding vehicle", 3);
            vehicles.add(v);
            FileManager.save(vehicles, totalIncome);
            gap(1);
            text(GREEN + " Vehicle added successfully!" + RESET);
        }
    }



    private static void viewVehicles() {
        if (vehicles.isEmpty()) {
            text("No vehicles available.");
            return;
        }

        bigHeading("Vehicle List");

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
    }

    private static void rentVehicle(Scanner sc) {
        sectionHeading("Rent Vehicle");

        String id = inputCentered("Enter Vehicle ID to rent: ", sc);
        Vehicle v = searchById(id);

        if (v == null || !v.isAvailable()) {
            text(RED + "Vehicle not available!" + RESET);
            return;
        }

        int days = Integer.parseInt(inputCentered("Enter number of rental days: ", sc));
        v.rentVehicle();
        double cost = v.calculateRentalCost(days);
        totalIncome += cost;

        FileManager.save(vehicles, totalIncome);
        text("Rental cost : Rs. " + YELLOW + cost + RESET);
    }

    private static void returnVehicle(Scanner sc) {
        sectionHeading("Return Vehicle");

        String id = inputCentered("Enter Vehicle ID to return: ", sc);
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

        String id = inputCentered("Enter Vehicle ID to search: ", sc);
        Vehicle v = searchById(id);

        if (v == null) {
            text("Vehicle not found!");
        } else {
            v.displayDetails();
        }
    }

    private static Vehicle searchById(String id) {
        for (Vehicle v : vehicles)
            if (v.getVehicleId().equalsIgnoreCase(id))
                return v;
        return null;
    }
}
