import java.util.HashMap;


public interface Customer {

    /**
     * Gets the name of the customer.
     *
     * @return The customer's name.
     */
    String getName();

    /**
     * Gets the current monetary balance of the customer.
     *
     * @return The customer's current balance.
     */
    double getBalance();

    double getStock();

    /**
     * Gets the customer's inventory of purchased products.
     * The map's key is the InventoryItem (representing the product type)
     * and the value is the quantity of that product owned by the customer.
     * Implementations should consider returning an unmodifiable view or a copy
     * of the internal inventory to maintain encapsulation.
     *
     * @return A map representing the customer's inventory.
     */
    HashMap<InventoryItem, Double> getInventory(); // Or use Map<Product, Double> if customers only buy Products





     String getCustomerID();
}

