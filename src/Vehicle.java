public abstract class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private double baseRatePerDay;
    private boolean isAvailable;

    public Vehicle(String vehicleId, String brand, String model, double baseRatePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.baseRatePerDay = baseRatePerDay;
        this.isAvailable = true;
    }


    public String getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }


    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }



    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }


    public double getBaseRatePerDay() {
        return baseRatePerDay;
    }
    public void setBaseRatePerDay(double baseRatePerDay) {
        this.baseRatePerDay = baseRatePerDay;
    }


    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    public void displayDetails() {
        System.out.println("ID: " + vehicleId +
                ", Brand: " + brand +
                ", Model: " + model +
                ", Rate: " + baseRatePerDay +
                ", Available: " + isAvailable);
    }

    public void rentVehicle() {
        if(isAvailable) {
            isAvailable = false;

        } else {
            System.out.println("Vehicle is already rented!");
        }
    }

    public void returnVehicle() {
        if(!isAvailable) {
            isAvailable = true;
            System.out.println("Vehicle returned successfully.");
        } else {
            System.out.println("Vehicle was not rented!");
        }
    }

    public abstract double calculateRentalCost(int days);
}
