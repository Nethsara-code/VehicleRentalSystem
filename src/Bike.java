public class Bike extends Vehicle {
    private int engineCapacityCC;

    public Bike(String id, String brand, String model, double rate, int engineCC) {
        super(id, brand, model, rate);
        this.engineCapacityCC = engineCC;
    }


    public double calculateRentalCost(int days) {
        return getBaseRatePerDay() * days + (engineCapacityCC * 0.5 * days);
    }


    public void displayDetails() {
        super.displayDetails();
        System.out.println("Engine Capacity: " + engineCapacityCC + "CC");
    }
}
