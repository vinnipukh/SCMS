import java.util.HashMap;

public class ProductDesign {

    private int byproductAmount;
    private int rawmaterialAmount;

    private Product product;
    private ByProduct byproduct;
    private RawMaterial rawmaterial;
    private RawMaterial rawmaterial2;
    
    private HashMap<InventoryItem,Integer> inputRequirements;
    private HashMap<InventoryItem,Integer> outputProducts;

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


    public int getByproductAmount() {
        return this.byproductAmount;
    }

    public void setByproductAmount(int byproductAmount) {
        this.byproductAmount = byproductAmount;
    }

    public int getRawmaterialAmount() {
        return this.rawmaterialAmount;
    }

    public void setRawmaterialAmount(int rawmaterialAmount) {
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

    public HashMap<InventoryItem,Integer> getinputRequirements() {
        return inputRequirements;
    }

    public void setinputRequirements(HashMap<InventoryItem,Integer> inputRequirements) {
        this.inputRequirements = inputRequirements;
    }
    public double getProductionCost() {
        return this.productionCost;
    }

    public void setProductionCost(double productionCost) {
        this.productionCost = productionCost;
    }

    
    public boolean canProduce(HashMap<InventoryItem, Integer> availableInventory, int amountToProduce) {
        for (InventoryItem item : inputRequirements.keySet()) {
            int required = inputRequirements.get(item) * amountToProduce;
            int available = availableInventory.getOrDefault(item, 0);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    public HashMap<InventoryItem, Integer> getConsumedInputs(int amountToProduce) {
        HashMap<InventoryItem, Integer> consumed = new HashMap<>();
        for (InventoryItem item : inputRequirements.keySet()) {
            consumed.put(item, inputRequirements.get(item) * amountToProduce);
        }
        return consumed;
    }

    public HashMap<InventoryItem, Integer> getProducedOutputs(int amountToProduce) {
        HashMap<InventoryItem, Integer> produced = new HashMap<>();
        for (InventoryItem item : outputProducts.keySet()) {
            produced.put(item, outputProducts.get(item) * amountToProduce);
        }
        return produced;
    }

    public void addInputRequirement(InventoryItem item, int amount) {
        inputRequirements.put(item, amount);
    }

    public void addOutputProduct(InventoryItem item, int amount) {
        outputProducts.put(item, amount);
    }

    public void consumeInputsFromInventory(HashMap<InventoryItem, Integer> inventory, int amountToProduce) {
        for (InventoryItem item : inputRequirements.keySet()) {
            int required = inputRequirements.get(item) * amountToProduce;
            inventory.put(item, inventory.getOrDefault(item, 0) - required);
        }
    }

    public void addOutputsToInventory(HashMap<InventoryItem, Integer> inventory, int amountToProduce) {
        for (InventoryItem item : outputProducts.keySet()) {
            int produced = outputProducts.get(item) * amountToProduce;
            inventory.put(item, inventory.getOrDefault(item, 0) + produced);
        }
    }

   
}
