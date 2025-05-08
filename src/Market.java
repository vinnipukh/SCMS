import java.util.HashMap;
import java.util.Map;

public class Market implements Customer{
    private String name;
    private final String marketID;
    private static int nextID = 0;
    private double balance;
    private HashMap<Product, Integer> inventory = new HashMap<>();
    private HashMap<Product, Double> productPrices = new HashMap<>();
    
    public Market(String name, double balance){
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
    public String getCustomerID() {
        return marketID;
    }
    
    // Add product to inventory (or increase amount)
    public void addProduct(Product product, int amount, double price) {
        if (product == null || amount <= 0) throw new IllegalArgumentException("Invalid product or amount");
        inventory.put(product, inventory.getOrDefault(product, 0) + amount);
        // Set price if not already set
        if (!productPrices.containsKey(product)) {
            productPrices.put(product, price);
        }
    }

    // Set price for a product in inventory
    public void setProductPrice(Product product, double price) {
        if (!inventory.containsKey(product)) throw new IllegalArgumentException("Product not in inventory");
        productPrices.put(product, price);
    }

    // Get price for a product
    public double getProductPrice(Product product) {
        return productPrices.getOrDefault(product, product.getProductionCost());
    }

    // Get inventory map
    public Map<Product, Integer> getInventory() {
        return inventory;
    }

    // Get product prices map
    public Map<Product, Double> getProductPrices() {
        return productPrices;
    }

    // Buy product from another market or factory
    public void buyProduct(Product product, int amount, double pricePerUnit, Customer seller) {
        double totalCost = amount * pricePerUnit;
        if (balance < totalCost) throw new IllegalStateException("Insufficient funds");
        // Only allow buying from Factory or Market (not RawMaterialProducer)
        if (!(seller instanceof Factory) && !(seller instanceof Market)) {
            throw new IllegalArgumentException("Can only buy from factories or other markets");
        }
        // Remove from seller's inventory (if Market)
        if (seller instanceof Market) {
            Market marketSeller = (Market) seller;
            int sellerStock = marketSeller.inventory.getOrDefault(product, 0);
            if (sellerStock < amount) throw new IllegalStateException("Seller does not have enough stock");
            marketSeller.inventory.put(product, sellerStock - amount);
            if (marketSeller.inventory.get(product) == 0) {
                marketSeller.inventory.remove(product);
                marketSeller.productPrices.remove(product);
            }
            marketSeller.balance += totalCost;
        } else if (seller instanceof Factory) {
            Factory factorySeller = (Factory) seller;
            Map<String, Integer> factoryProducts = factorySeller.getProducts();
            int sellerStock = factoryProducts.getOrDefault(product.getName(), 0);
            if (sellerStock < amount) throw new IllegalStateException("Factory does not have enough stock");
            factoryProducts.put(product.getName(), sellerStock - amount);
            factorySeller.setBalance(factorySeller.getBalance() + totalCost);
        }
        // Add to this market's inventory
        addProduct(product, amount, pricePerUnit);
        balance -= totalCost;
    }

    // Sell product from own inventory
    public void sellProduct(Product product, int amount, double pricePerUnit, Market buyer) {
        int stock = inventory.getOrDefault(product, 0);
        if (stock < amount) throw new IllegalStateException("Not enough stock to sell");
        double totalRevenue = amount * pricePerUnit;
        inventory.put(product, stock - amount);
        if (inventory.get(product) == 0) {
            inventory.remove(product);
            productPrices.remove(product);
        }
        balance += totalRevenue;
        buyer.addProduct(product, amount, pricePerUnit);
        buyer.balance -= totalRevenue;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
