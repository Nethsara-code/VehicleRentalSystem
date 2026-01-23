public class Van extends Vehicle {

    private double cargoCapacityKg;

    public Van(String id, String brand, String model, double rate, double cargoKg) {
        super(id, brand, model, rate);
        this.cargoCapacityKg = cargoKg;
    }

    public double calculateRentalCost(int days) {
        return getBaseRatePerDay() * days + (cargoCapacityKg * 0.2 * days);
    }

}
