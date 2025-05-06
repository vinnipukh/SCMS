public class Product extends InventoryItem{
    private double productionCost;

    public Product(String name, double productionCost) {
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
        return super.toString()+ " Product{" +
                "productionCost=" + productionCost +
                '}';
    }
}
