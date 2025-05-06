public class ByProduct extends InventoryItem{
    private double disposalCost;

    public ByProduct(String name, double disposalCost) {
        super(name);
        this.disposalCost = disposalCost;
    }

    public double getDisposalCost() {
        return this.disposalCost;
    }

    public void setDisposalCost(double disposalCost) {
        this.disposalCost = disposalCost;
    }

    @Override
    public String toString() {
        return super.toString()+ " ByProduct{" +
                "disposalCost=" + disposalCost +
                '}';
    }
}
