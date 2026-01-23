public abstract class Vehicle{

    private String vehicleId;
    private String brand;
    private  String model;
    private double baseRatePerDay;
    private boolean isAvailable;

    public Vehicle(String Vehicle, String brand, String model, double baseRatePerDay){
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.baseRatePerDay = baseRatePerDay;
        this.isAvailable = isAvailable = true;
    }

    public String getVehicleId(){
        return vehicleId;
    }

    public String getBrand(){
        return brand;
    }

    public String getModel(){
        return model;
    }

    public double getBaseRatePerDay(){
        return baseRatePerDay;
    }

    public boolean isAvailable(){
        return isAvailable;
    }

    public void rentVehicle(){
        isAvailable = false;
    }

    public void returnVehicle(){
        isAvailable = true;
    }

    public void displayDetails(){
        System.out.println("ID : "+vehicleId+
                "Brand : " + brand+
                "Model : " + model+
                "Rate : "+ baseRatePerDay+
                "Available : " + isAvailable

                );
    }

    public abstract double calculateRentalCost(int days);


}