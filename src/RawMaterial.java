public class RawMaterial extends InventoryItem{

    private  double productionCost;

    public RawMaterial(String name){
        super(name);

    }

    public double getProductionCost() {
        return this.productionCost;
    }

    public void setProductionCost(double productionCost) {
        if (productionCost < 0) {
            throw new IllegalArgumentException("Production cost cannot be negative.");
        }
        this.productionCost = productionCost;
    }

    @Override
    public String toString() {
        return super.toString()+ " RawMaterial{" +
                "productionCost=" + productionCost +
                '}';
    }
}
