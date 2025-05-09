
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Market implements Customer { // Customer interface implies getName, getBalance, getCustomerID
    private String name;
    private final String marketID;
    private static int nextID = 0;
    private double balance;
    // Using Product object as key. Ensure Product has good equals() and hashCode().
    // InventoryItem (superclass of Product) equals/hashCode is based on name.
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
        // Set price if not already set, or if it's at default cost (product.getProductionCost()).
        // The project description: "Default price is equal to the cost of the product."
        // And "In set prices you can see the products in your storage... You can select any one and enter the price field... to change the price"
        if (!productPrices.containsKey(product) || productPrices.get(product) == product.getProductionCost()) {
            productPrices.put(product, price);
        }
    }

    public void setProductPrice(Product product, double price) {
        if (!inventory.containsKey(product) && inventory.getOrDefault(product,0) == 0) {
            // According to PDF, can only set price for products in storage.
            throw new IllegalArgumentException("Product '" + product.getName() + "' not in stock to set price for.");
        }
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        productPrices.put(product, price);
    }

    public double getProductPrice(Product product) {
        // Project: "Default price is equal to the cost of the product."
        return productPrices.getOrDefault(product, product.getProductionCost());
    }

    public Map<Product, Integer> getInventory() {
        return new HashMap<>(inventory); // Return a copy for safety
    }

    public Map<Product, Double> getProductPrices() {
        return new HashMap<>(productPrices); // Return a copy
    }

    // This method is for when THIS market BUYS from another Factory or Market
    public void buyProduct(Product product, int amount, double pricePerUnit, Customer seller) {
        double totalCost = amount * pricePerUnit;
        if (balance < totalCost) throw new IllegalStateException("Insufficient funds to buy. Required: " + totalCost + ", Have: " + balance);

        if (!(seller instanceof Factory) && !(seller instanceof Market)) {
            throw new IllegalArgumentException("This market can only buy from factories or other markets.");
        }

        // Simulate seller's stock deduction and balance increase
        // This part assumes direct manipulation of the seller.
        // A better design might involve the seller having its own "sell" method called here.
        if (seller instanceof Market) {
            Market marketSeller = (Market) seller;
            // It's better if marketSeller has a method like marketSeller.executeSaleToMarket(product, amount, thisMarket);
            // For now, we adapt to the existing structure slightly.
            int sellerStock = marketSeller.inventory.getOrDefault(product, 0);
            if (sellerStock < amount) throw new IllegalStateException("Seller market " + seller.getName() + " does not have enough stock of " + product.getName());

            marketSeller.inventory.put(product, sellerStock - amount); // Seller reduces stock
            if (marketSeller.inventory.get(product) == 0) marketSeller.inventory.remove(product);
            marketSeller.balance += totalCost; // Seller gains money
        } else if (seller instanceof Factory) {
            Factory factorySeller = (Factory) seller;
            // Similar to above, factorySeller should ideally have a method like factorySeller.executeSaleToMarket(...)
            Map<String, Integer> factoryProducts = factorySeller.getProducts(); // Products stored by name string
            int sellerStock = factoryProducts.getOrDefault(product.getName(), 0);
            if (sellerStock < amount) throw new IllegalStateException("Seller factory " + seller.getName() + " does not have enough stock of " + product.getName());

            factoryProducts.put(product.getName(), sellerStock - amount); // Factory reduces stock
            factorySeller.setBalance(factorySeller.getBalance() + totalCost); // Factory gains money
        }

        addProduct(product, amount, pricePerUnit); // Add to THIS market's inventory (price is what this market paid)
        balance -= totalCost; // THIS market loses money
    }

    // This is the existing method for Market-to-Market selling.
    // The `buyer` Market calls its own `buyProduct` method, which then interacts with `this` (seller) Market.
    // So, this method might be redundant if buyProduct handles the interaction correctly,
    // OR it's meant to be called by the seller if the buyer initiates differently.
    // For consistency with how ConcreteCustomer.buyProductFromMarket works,
    // the BUYER should initiate and the SELLER should have a method to finalize its side of the sale.
    public void sellProduct(Product product, int amount, double pricePerUnit, Market buyerMarket) {
        int stock = inventory.getOrDefault(product, 0);
        if (stock < amount) throw new IllegalStateException("Not enough stock to sell " + product.getName());

        double totalRevenue = amount * pricePerUnit; // This is what the current market (seller) receives

        // Update this (seller) market's inventory and balance
        inventory.put(product, stock - amount);
        if (inventory.get(product) == 0) {
            inventory.remove(product);
            // productPrices.remove(product); // Optional: remove price if stock is zero, but PDF implies products in storage for set price.
        }
        balance += totalRevenue;

        // The buyerMarket needs to handle its side: deduct its balance, add to its inventory.
        // This direct manipulation of buyerMarket is not ideal.
        // buyerMarket.addProduct(product, amount, pricePerUnit); // Buyer adds product (at the price they paid)
        // buyerMarket.balance -= totalRevenue; // Buyer loses money
        // This should be: buyerMarket.processPurchase(product, amount, pricePerUnit); or similar.
        // For now, let's assume the existing MarketDetailPage handles the buyer's side when it calls this.
        // However, to align with the Customer model, let's make this method primarily for the SELLER's update.
    }

    /**
     * NEW METHOD: Called when this market sells a product to a ConcreteCustomer.
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
        if (product == null || buyer == null) { // buyer is technically not needed for *this* market's update
            throw new IllegalArgumentException("Product or buyer cannot be null for sellProductToCustomer.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to sell must be positive.");
        }

        int currentStock = this.inventory.getOrDefault(product, 0);
        // This check is a safeguard; ConcreteCustomer.buyProductFromMarket should have already checked.
        if (currentStock < amount) {
            throw new IllegalStateException("Internal error or race condition: Market " + this.name +
                    " has insufficient stock of " + product.getName() +
                    " (Have: " + currentStock + ", Selling: " + amount + ") for customer " + buyer.getName());
        }

        double pricePerUnit = getProductPrice(product); // Use the market's set selling price for this product
        double totalRevenue = pricePerUnit * amount;

        // Update market's inventory
        this.inventory.put(product, currentStock - amount);
        if (this.inventory.get(product) == 0) {
            this.inventory.remove(product);
            // If stock is 0, should we remove from productPrices?
            // PDF "Set Prices": "you can see the products in your storage".
            // If not in storage, can't set price. So, removing price makes sense if it cannot be set.
            // However, if it can be restocked, keeping price might be useful.
            // Let's keep productPrices entry for now, consistent with sellProduct to Market.
        }

        // Update market's balance
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