import java.util.HashMap;

public class RawMaterialProducer  {
    private String name;
    private final String customerID;
    private static int nextID = 0;
    private double balance;
    private  HashMap<RawMaterial,Double> Inventory;
    public RawMaterialProducer(){

        this.customerID = "RMP_"+nextID++;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerID() {
        return customerID;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public double getStock(RawMaterial rawMaterial){
        return Inventory.get(rawMaterial);
    }

    public void addProducibleMaterial(RawMaterial material){

    }

    public HashMap<RawMaterial, Double> getInventory(){
        return Inventory;

    }


}
