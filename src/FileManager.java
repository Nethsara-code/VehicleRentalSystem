import java.io.*;
import java.util.ArrayList;

public class FileManager {

    private static final String FILE_NAME = "../data.txt";

    public static void save(ArrayList<Vehicle> vehicles, double income) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {

            pw.println(income);

            for (Vehicle v : vehicles) {
                pw.println(v.getClass().getSimpleName() + "," +
                        v.getVehicleId() + "," +
                        v.getBrand() + "," +
                        v.getModel() + "," +
                        v.getBaseRatePerDay() + "," +
                        v.isAvailable());
            }

        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    public static double load(ArrayList<Vehicle> vehicles) {
        double income = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            income = Double.parseDouble(br.readLine());
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String type = data[0];
                String id = data[1];
                String brand = data[2];
                String model = data[3];
                double rate = Double.parseDouble(data[4]);
                boolean available = Boolean.parseBoolean(data[5]);

                Vehicle v = null;

                if (type.equals("Car")) {
                    v = new Car(id, brand, model, rate, 4);
                } else if (type.equals("Bike")) {
                    v = new Bike(id, brand, model, rate, 150);
                } else if (type.equals("Van")) {
                    v = new Van(id, brand, model, rate, 500);
                }

                if (v != null && !available) {
                    v.rentVehicle();
                }

                vehicles.add(v);
            }

        } catch (Exception e) {
            System.out.println("No previous data found.");
        }

        return income;
    }
}
