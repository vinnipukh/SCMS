import java.util.HashMap;

public class RawMaterialProducer implements Customer {
    private String producerName;
    private final String producerID;
    private static int nextID = 0;
    private double balance;
    private double storageCapacity;

    private RawMaterial materialProduced;
    private double currentStockOfMaterial;
    private double sellingPrice;

    /**
     * Constructs a RawMaterialProducer.
     *
     * @param producerName      The name of the producer.
     * @param initialBalance    The initial funds of the producer.
     * @param storageCapacity   The maximum amount of raw material the producer can store.
     * @param materialProduced  The RawMaterial object that this producer will create (contains name and production cost).
     * @param sellingPrice      The price at which this producer sells one unit of its raw material.
     */
    public RawMaterialProducer(String producerName, double initialBalance, double storageCapacity,
                               RawMaterial materialProduced, double sellingPrice) {
        if (materialProduced == null) {
            throw new IllegalArgumentException("Material to be produced cannot be null.");
        }
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative.");
        }
        if (storageCapacity < 0) {
            throw new IllegalArgumentException("Storage capacity cannot be negative.");
        }
        if (initialBalance < 0) {
            // Or allow it if producers can start in debt, adjust as per requirements
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        this.producerName = producerName;
        this.producerID = "RMP_" + nextID++;
        this.balance = initialBalance;
        this.storageCapacity = storageCapacity;
        this.materialProduced = materialProduced;
        this.sellingPrice = sellingPrice;
        this.currentStockOfMaterial = 0.0; // Producers typically start with 0 stock
    }

    @Override
    public String getName() {
        return this.producerName;
    }

    @Override
    public double getBalance() {
        return this.balance;
    }

    /**
     * Gets the current stock of the single raw material this producer produces.
     * Implements Customer.getStock().
     */
    public double getStock() {
        return this.currentStockOfMaterial;
    }

    /**
     * Gets the inventory of this producer.
     * For a RawMaterialProducer, this map contains a single entry:
     * the raw material it produces and the current stock of that material.
     * Implements Customer.getInventory().
     */

    public HashMap<InventoryItem, Double> getInventory() {
        HashMap<InventoryItem, Double> inventoryMap = new HashMap<>();
        // materialProduced should not be null due to constructor check
        inventoryMap.put(this.materialProduced, this.currentStockOfMaterial);
        return inventoryMap;
    }

    @Override
    public String getCustomerID() {
        return this.producerID;
    }

    // --- RawMaterialProducer specific methods ---

    public String getProducerID() {
        return producerID;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public void setBalance(double balance) { // e.g., for admin adjustments
        this.balance = balance;
    }

    public double getStorageCapacity() {
        return this.storageCapacity;
    }

    public void setStorageCapacity(double storageCapacity) {
        if (storageCapacity < 0) {
            throw new IllegalArgumentException("Storage capacity cannot be negative.");
        }
        if (storageCapacity < this.currentStockOfMaterial) {
            System.err.println("Warning: New capacity (" + storageCapacity +
                    ") is less than current stock (" + this.currentStockOfMaterial +
                    "). Stock remains, but no new production possible until space clears or capacity increases.");
        }
        this.storageCapacity = storageCapacity;
    }

    public RawMaterial getMaterialProduced() {
        return this.materialProduced;
    }

    public double getSellingPrice() {
        return this.sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative.");
        }
        this.sellingPrice = sellingPrice;
    }

    /**
     * Produces a given amount of the raw material.
     * This action costs funds and increases stock.
     *
     * @param amountToProduce The amount of raw material to produce. Must be positive.
     * @throws IllegalArgumentException if amountToProduce is not positive.
     * @throws IllegalStateException if there are insufficient funds for production,
     */
    public void produce(double amountToProduce) {
        if (amountToProduce <= 0) {
            throw new IllegalArgumentException("Amount to produce must be positive. Attempted: " + amountToProduce);
        }

        double costPerUnit = this.materialProduced.getProductionCost();
        double totalProductionCost = amountToProduce * costPerUnit;

        if (this.balance < totalProductionCost) {
            throw new IllegalStateException("Insufficient funds for " + producerName + " to produce " + amountToProduce +
                    " of " + this.materialProduced.getName() +
                    ". Required: " + totalProductionCost + ", Available: " + this.balance);
        }

        if ((this.currentStockOfMaterial + amountToProduce) > this.storageCapacity) {
            double availableSpace = this.storageCapacity - this.currentStockOfMaterial;
            throw new IllegalStateException("Insufficient storage capacity for " + producerName + " to produce " + amountToProduce +
                    " of " + this.materialProduced.getName() +
                    ". Available space: " + availableSpace + ", Needed for this batch: " + amountToProduce);
        }

        this.balance -= totalProductionCost;
        this.currentStockOfMaterial += amountToProduce;
        // System.out.println(this.producerName + " produced " + amountToProduce + " of " + this.materialProduced.getName());
    }

    /**
     * Sells a given amount of the produced raw material from stock.
     * This action generates revenue and decreases stock.
     * The actual transfer to a buyer and buyer's payment deduction would be handled externally (e.g., by a Controller).
     *
     * @param amountToSell The amount of raw material to sell. Must be positive.
     * @return The total revenue generated from this sale (amountToSell * sellingPrice).
     * @throws IllegalArgumentException if amountToSell is not positive.
     * @throws IllegalStateException if there is insufficient stock to meet the sale.
     */
    public double sell(double amountToSell) {
        if (amountToSell <= 0) {
            throw new IllegalArgumentException("Amount to sell must be positive. Attempted: " + amountToSell);
        }
        if (this.currentStockOfMaterial < amountToSell) {
            throw new IllegalStateException("Insufficient stock for " + producerName + " to sell " + amountToSell +
                    " of " + this.materialProduced.getName() +
                    ". Available: " + this.currentStockOfMaterial + ", Requested: " + amountToSell);
        }

        this.currentStockOfMaterial -= amountToSell;
        double revenue = amountToSell * this.sellingPrice;
        this.balance += revenue;
        // System.out.println(this.producerName + " sold " + amountToSell + " of " + this.materialProduced.getName() + " for " + revenue);
        return revenue;
    }

    @Override
    public String toString() {
        return "RawMaterialProducer {" +
                "ID='" + producerID + '\'' +
                ", Name='" + producerName + '\'' +
                ", Balance=" + String.format("%.2f", balance) +
                ", Produces='" + materialProduced.getName() + '\'' +
                " (Cost: " + String.format("%.2f", materialProduced.getProductionCost()) + ")" +
                ", SellsAt=" + String.format("%.2f", sellingPrice) +
                ", Stock=" + String.format("%.2f", currentStockOfMaterial) +
                " / Capacity=" + String.format("%.2f", storageCapacity) +
                '}';
    }
}