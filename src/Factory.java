import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Factory implements Customer {
    private final String factoryID;
    private final String name;
    private double balance;
    private int capacity;

    private Map<String, Integer> products;
    private Map<String, Integer> byproducts;
    private Map<String, Integer> rawMaterials;

    private List<ProductDesign> designs;

    private static int nextID = 0;

    public Factory(String name, double initialBalance, int capacity) {
        this.name = name;
        this.balance = initialBalance;
        this.capacity = capacity;
        this.factoryID = "FACTORY_" + nextID++;
        this.products = new HashMap<>();
        this.byproducts = new HashMap<>();
        this.rawMaterials = new HashMap<>();
        this.designs = new ArrayList<>();
    }

    public String getFactoryID() { return factoryID; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public int getCapacity() { return capacity; }

    public void setBalance(double balance) { this.balance = balance; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Map<String, Integer> getProducts() { return products; }
    public Map<String, Integer> getByproducts() { return byproducts; }
    public Map<String, Integer> getRawMaterials() { return rawMaterials; }
    public List<ProductDesign> getDesigns() { return designs; }

    public void addProduct(String name, int amount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }
        int currentTotal = getTotalInventory();
        if (currentTotal + amount > capacity) {
            throw new IllegalStateException("Insufficient storage to add product. Available: " + (capacity - currentTotal));
        }
        products.put(name, products.getOrDefault(name, 0) + amount);
    }

    public void addByproduct(String name, int amount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Byproduct name cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }
        int currentTotal = getTotalInventory();
        if (currentTotal + amount > capacity) {
            throw new IllegalStateException("Insufficient storage to add byproduct. Available: " + (capacity - currentTotal));
        }
        byproducts.put(name, byproducts.getOrDefault(name, 0) + amount);
    }

    public void addRawMaterial(String name, int amount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Raw material name cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }
        int currentTotal = getTotalInventory();
        if (currentTotal + amount > capacity) {
            throw new IllegalStateException("Insufficient storage to add raw material. Available: " + (capacity - currentTotal));
        }
        rawMaterials.put(name, rawMaterials.getOrDefault(name, 0) + amount);
    }

    public void addDesign(ProductDesign design) {
        if (design == null) {
            throw new IllegalArgumentException("Design cannot be null.");
        }
        designs.add(design);
    }

    public int getTotalInventory() {
        return products.values().stream().mapToInt(Integer::intValue).sum()
            + byproducts.values().stream().mapToInt(Integer::intValue).sum()
            + rawMaterials.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void buyProduct(String name, int amount, double pricePerUnit) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to buy must be positive.");
        }
        double totalCost = amount * pricePerUnit;
        if (balance < totalCost) {
            throw new IllegalStateException("Insufficient funds to buy product. Required: " + totalCost + ", Available: " + balance);
        }
        int currentTotal = getTotalInventory();
        if (currentTotal + amount > capacity) {
            throw new IllegalStateException("Insufficient storage to buy product. Available: " + (capacity - currentTotal));
        }
        balance -= totalCost;
        rawMaterials.put(name, rawMaterials.getOrDefault(name, 0) + amount);
    }

    public void produceProduct(ProductDesign design, int amount) {
        if (design == null) {
            throw new IllegalArgumentException("Design cannot be null.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to produce must be positive.");
        }
        for (InventoryItem item : design.getinputRequirements().keySet()) {
            double required = design.getinputRequirements().get(item) * amount;
            int available = rawMaterials.getOrDefault(item.getName(), 0);
            if (available < required) {
                throw new IllegalStateException("Insufficient raw material: " + item.getName() + ". Required: " + required + ", Available: " + available);
            }
        }
        double totalCost = design.getProductionCost() * amount;
        if (balance < totalCost) {
            throw new IllegalStateException("Insufficient funds to produce product. Required: " + totalCost + ", Available: " + balance);
        }
        int currentTotal = getTotalInventory();
        int outputAmount = amount; // Assuming 1:1 output for main product
        if (currentTotal + outputAmount > capacity) {
            throw new IllegalStateException("Insufficient storage to produce product. Available: " + (capacity - currentTotal));
        }
        for (InventoryItem item : design.getinputRequirements().keySet()) {
            double required = design.getinputRequirements().get(item) * amount;
            rawMaterials.put(item.getName(), rawMaterials.get(item.getName()) - (int)required);
        }
        balance -= totalCost;
        String productName = design.getProduct().getName();
        products.put(productName, products.getOrDefault(productName, 0) + amount);
        if (design.getByproduct() != null && design.getByproductAmount() > 0) {
            String byproductName = design.getByproduct().getName();
            int byproductAmount = (int)(design.getByproductAmount() * amount);
            byproducts.put(byproductName, byproducts.getOrDefault(byproductName, 0) + byproductAmount);
        }
    }

    public void destroyByproduct(String name, int amount, double costPerUnit) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Byproduct name cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to destroy must be positive.");
        }
        int available = byproducts.getOrDefault(name, 0);
        if (available < amount) {
            throw new IllegalStateException("Insufficient byproduct to destroy. Available: " + available + ", Requested: " + amount);
        }
        double totalCost = costPerUnit * amount;
        if (balance < totalCost) {
            throw new IllegalStateException("Insufficient funds to destroy byproduct. Required: " + totalCost + ", Available: " + balance);
        }
        byproducts.put(name, available - amount);
        balance -= totalCost;
    }

    @Override
    public String getCustomerID() {
        return factoryID;
    }
} 