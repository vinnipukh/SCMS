
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Market implements Customer {
    private String name;
    private final String marketID;
    private static int nextID = 0;
    private double balance;
    private HashMap<Product, Integer> inventory = new HashMap<>();
    private HashMap<Product, Double> productPrices = new HashMap<>();

    public Market(String name, double balance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Market name cannot be empty.");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.name = name;
        this.marketID = "Market_" + nextID++;
        this.balance = balance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public String getCustomerID() { // This is marketID, as per Customer interface usage
        return marketID;
    }

    public String getMarketID() { // Specific getter for clarity
        return marketID;
    }

    public void addProduct(Product product, int amount, double price) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null.");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");

        inventory.put(product, inventory.getOrDefault(product, 0) + amount);
        if (!productPrices.containsKey(product) || productPrices.get(product) == product.getProductionCost()) {
            productPrices.put(product, price);
        }
    }

    public void setProductPrice(Product product, double price) {
        if (!inventory.containsKey(product) && inventory.getOrDefault(product,0) == 0) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' not in stock to set price for.");
        }
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        productPrices.put(product, price);
    }

    public double getProductPrice(Product product) {
        return productPrices.getOrDefault(product, product.getProductionCost());
    }

    public Map<Product, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public Map<Product, Double> getProductPrices() {
        return new HashMap<>(productPrices);
    }

    public void buyProduct(Product product, int amount, double pricePerUnit, Customer seller) {
        double totalCost = amount * pricePerUnit;
        if (balance < totalCost) throw new IllegalStateException("Insufficient funds to buy. Required: " + totalCost + ", Have: " + balance);

        if (!(seller instanceof Factory) && !(seller instanceof Market)) {
            throw new IllegalArgumentException("This market can only buy from factories or other markets.");
        }

        if (seller instanceof Market) {
            Market marketSeller = (Market) seller;
            int sellerStock = marketSeller.inventory.getOrDefault(product, 0);
            if (sellerStock < amount) throw new IllegalStateException("Seller market " + seller.getName() + " does not have enough stock of " + product.getName());

            marketSeller.inventory.put(product, sellerStock - amount);
            if (marketSeller.inventory.get(product) == 0) marketSeller.inventory.remove(product);
            marketSeller.balance += totalCost;
        } else if (seller instanceof Factory) {
            Factory factorySeller = (Factory) seller;
            Map<String, Integer> factoryProducts = factorySeller.getProducts();
            int sellerStock = factoryProducts.getOrDefault(product.getName(), 0);
            if (sellerStock < amount) throw new IllegalStateException("Seller factory " + seller.getName() + " does not have enough stock of " + product.getName());

            factoryProducts.put(product.getName(), sellerStock - amount);
            factorySeller.setBalance(factorySeller.getBalance() + totalCost);
        }

        addProduct(product, amount, pricePerUnit);
        balance -= totalCost;
    }

    public void sellProduct(Product product, int amount, double pricePerUnit, Market buyerMarket) {
        int stock = inventory.getOrDefault(product, 0);
        if (stock < amount) throw new IllegalStateException("Not enough stock to sell " + product.getName());

        double totalRevenue = amount * pricePerUnit; // This is what the current market (seller) receives

        inventory.put(product, stock - amount);
        if (inventory.get(product) == 0) {
            inventory.remove(product);
        }
        balance += totalRevenue;

    }

    /**
     * N Called when this market sells a product to a Customers.
     * This method updates only THIS market's state (inventory and balance).
     * The customer's state (balance, inventory) is handled by the customer object
     * before this method is called.
     *
     * @param product The product sold.
     * @param amount The quantity sold.
     * @param buyer The customer who bought the product (passed for logging or future use, not strictly needed for market update here).
     * @throws IllegalArgumentException if inputs are invalid.
     * @throws IllegalStateException if market doesn't have enough stock (should be pre-checked by customer).
     */
    public void sellProductToCustomer(Product product, int amount, Customers buyer) {
        if (product == null || buyer == null) {
            throw new IllegalArgumentException("Product or buyer cannot be null for sellProductToCustomer.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to sell must be positive.");
        }

        int currentStock = this.inventory.getOrDefault(product, 0);
        if (currentStock < amount) {
            throw new IllegalStateException("Internal error or race condition: Market " + this.name +
                    " has insufficient stock of " + product.getName() +
                    " (Have: " + currentStock + ", Selling: " + amount + ") for customer " + buyer.getName());
        }

        double pricePerUnit = getProductPrice(product);
        double totalRevenue = pricePerUnit * amount;

        this.inventory.put(product, currentStock - amount);
        if (this.inventory.get(product) == 0) {
            this.inventory.remove(product);
        }

        this.balance += totalRevenue;
    }


    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Market name cannot be empty.");
        }
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Market market = (Market) o;
        return Objects.equals(marketID, market.marketID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketID);
    }

    @Override
    public String toString() {
        return name + " (ID: " + marketID + ")";
    }
}