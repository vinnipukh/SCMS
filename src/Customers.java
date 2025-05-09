// ================================================
// FILE: src/ConcreteCustomer.java
// ================================================
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Customers implements Customer {
    private String name;
    private double balance;
    private final String customerID;
    private Map<Product, Integer> inventory; // Product object as key
    private static int nextID = 0;

    public Customers(String name, double initialBalance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.name = name;
        this.balance = initialBalance;
        this.customerID = "CUST_" + nextID++;
        this.inventory = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.name = name;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        // For direct setting, we might allow any value,
        // but purchase logic will prevent going into unplanned debt.
        this.balance = balance;
    }

    @Override
    public String getCustomerID() {
        return customerID;
    }

    public Map<Product, Integer> getInventory() {
        // Return a defensive copy if you want to prevent external modification of the internal map
        return new HashMap<>(inventory);
    }

    public void addToInventory(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive.");
        }
        this.inventory.put(product, this.inventory.getOrDefault(product, 0) + quantity);
    }

    /**
     * Allows a customer to attempt a purchase from a given market.
     * This method checks for funds, stock, and then updates customer's balance/inventory
     * and tells the market to update its state.
     *
     * @param product The product to buy.
     * @param quantity The amount of the product to buy.
     * @param sellerMarket The market from which the product is being bought.
     * @throws IllegalArgumentException if inputs are invalid.
     * @throws IllegalStateException if the customer cannot afford the product or market doesn't have enough stock.
     */
    public void buyProductFromMarket(Product product, int quantity, Market sellerMarket) {
        if (product == null || sellerMarket == null) {
            throw new IllegalArgumentException("Product or seller market cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to buy must be positive.");
        }

        double pricePerUnit = sellerMarket.getProductPrice(product);
        if (pricePerUnit < 0) {
            throw new IllegalStateException("Product price from market cannot be negative.");
        }
        double totalCost = pricePerUnit * quantity;

        if (this.balance < totalCost) {
            throw new IllegalStateException("Insufficient funds. Required: " + String.format("%.2f", totalCost) +
                    ", Available: " + String.format("%.2f", this.balance));
        }

        // Check market stock (Market's inventory uses Product as key)
        int marketStock = sellerMarket.getInventory().getOrDefault(product, 0);
        if (marketStock < quantity) {
            throw new IllegalStateException("Market " + sellerMarket.getName() + " does not have enough stock of " + product.getName() +
                    ". Available: " + marketStock + ", Requested: " + quantity);
        }

        // If all checks pass, proceed with transaction
        this.balance -= totalCost;
        this.addToInventory(product, quantity);

        // Notify the market to update its inventory and balance
        sellerMarket.sellProductToCustomer(product, quantity, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customers that = (Customers) o;
        return Objects.equals(customerID, that.customerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerID);
    }

    @Override
    public String toString() {
        return name + " (ID: " + customerID + ", Balance: " + String.format("%.2f", balance) + ")";
    }
}