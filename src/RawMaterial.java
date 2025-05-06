public class RawMaterial extends InventoryItem{

    private  double productionCost;

    public RawMaterial(String name){
        super(name);
        this.productionCost = productionCost;

    }

    public double getProductionCost() {
        return this.productionCost;
    }

    public void setProductionCost(double productionCost) {
        this.productionCost = productionCost;
    }

    @Override
    public String toString() {
        return super.toString()+ " RawMaterial{" +
                "productionCost=" + productionCost +
                '}';
    }
}
