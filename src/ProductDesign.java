import java.util.HashMap;

public class ProductDesign {

    private double byproductAmount;
    private double rawmaterialAmount;

    private Product product;
    private ByProduct byproduct;
    private RawMaterial rawmaterial;
    private RawMaterial rawmaterial2;
    
    private HashMap<InventoryItem,Double> inputRequirements;
    private HashMap<InventoryItem,Double> outputProducts;

    private double productionCost;

    public ProductDesign(Product product,ByProduct byproduct,RawMaterial rawmaterial){
        this.product = product;
        this.byproduct = byproduct;
        this.rawmaterial = rawmaterial;
        this.inputRequirements = new HashMap<>();        
        this.outputProducts = new HashMap<>();
    }
    public ProductDesign(Product product1,ByProduct byproduct,RawMaterial rawmaterial,RawMaterial rawmaterial2){
        this(product1,byproduct,rawmaterial);
        this.rawmaterial2 = rawmaterial2;



    }


    public double getByproductAmount() {
        return this.byproductAmount;
    }

    public void setByproductAmount(double byproductAmount) {
        this.byproductAmount = byproductAmount;
    }

    public double getRawmaterialAmount() {
        return this.rawmaterialAmount;
    }

    public void setRawmaterialAmount(double rawmaterialAmount) {
        this.rawmaterialAmount = rawmaterialAmount;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ByProduct getByproduct() {
        return this.byproduct;
    }

    public void setByproduct(ByProduct byproduct) {
        this.byproduct = byproduct;
    }

    public RawMaterial getRawmaterial() {
        return this.rawmaterial;
    }

    public void setRawmaterial(RawMaterial rawmaterial) {
        this.rawmaterial = rawmaterial;
    }

    public HashMap<InventoryItem,Double> getinputRequirements() {
        return inputRequirements;
    }

    public void setinputRequirements(HashMap<InventoryItem,Double> inputRequirements) {
        this.inputRequirements = inputRequirements;
    }
    public double getProductionCost() {
        return this.productionCost;
    }

    public void setProductionCost(double productionCost) {
        this.productionCost = productionCost;
    }

    
    public boolean canProduce(HashMap<InventoryItem, Double> availableInventory, double amountToProduce) {
        for (InventoryItem item : inputRequirements.keySet()) {
            double required = inputRequirements.get(item) * amountToProduce;
            double available = availableInventory.getOrDefault(item, 0.0);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    public HashMap<InventoryItem, Double> getConsumedInputs(double amountToProduce) {
        HashMap<InventoryItem, Double> consumed = new HashMap<>();
        for (InventoryItem item : inputRequirements.keySet()) {
            consumed.put(item, inputRequirements.get(item) * amountToProduce);
        }
        return consumed;
    }

    public HashMap<InventoryItem, Double> getProducedOutputs(double amountToProduce) {
        HashMap<InventoryItem, Double> produced = new HashMap<>();
        for (InventoryItem item : outputProducts.keySet()) {
            produced.put(item, outputProducts.get(item) * amountToProduce);
        }
        return produced;
    }

    public void addInputRequirement(InventoryItem item, double amount) {
        inputRequirements.put(item, amount);
    }

    public void addOutputProduct(InventoryItem item, double amount) {
        outputProducts.put(item, amount);
    }

    public void consumeInputsFromInventory(HashMap<InventoryItem, Double> inventory, double amountToProduce) {
        for (InventoryItem item : inputRequirements.keySet()) {
            double required = inputRequirements.get(item) * amountToProduce;
            inventory.put(item, inventory.getOrDefault(item, 0.0) - required);
        }
    }

    public void addOutputsToInventory(HashMap<InventoryItem, Double> inventory, double amountToProduce) {
        for (InventoryItem item : outputProducts.keySet()) {
            double produced = outputProducts.get(item) * amountToProduce;
            inventory.put(item, inventory.getOrDefault(item, 0.0) + produced);
        }
    }

   
}
