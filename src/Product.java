public class Product extends InventoryItem{
    private double ProductionCost;

    public Product(String name, double productionCost) {
        super(name);
        ProductionCost = productionCost;
    }

    public double getProductionCost() {
        return ProductionCost;
    }

    public void setProductionCost(double productionCost) {
        ProductionCost = productionCost;
    }

    @Override
    public String toString() {
        return super.toString()+ " Product{" +
                "ProductionCost=" + ProductionCost +
                '}';
    }
}
