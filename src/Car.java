public class Car extends Vehicle {
    private int numberOfSeats;

    public Car(String id, String brand, String model, double rate, int seats) {
        super(id, brand, model, rate);
        this.numberOfSeats = seats;
    }


    public double calculateRentalCost(int days) {
        return getBaseRatePerDay() * days + (numberOfSeats * 200 * days);
    }


    public void displayDetails() {
        super.displayDetails();
        System.out.println("Number of seats: " + numberOfSeats);
    }
}