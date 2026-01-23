public class Bike extends Vehicle{

    private int engineCapacityCC;

    public Bike(String id, String brand, String model, double rate, int cc){
        super(id, brand, model, rate);
        this.engineCapacityCC = cc;
    }

    public double  calculateRentalCost(int days){
        return getBaseRatePerDay() * days + (engineCapacityCC * 0.5 * days);
    }


}


