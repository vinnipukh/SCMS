// ================================================
// FILE: src/MarketController.java
// ================================================
import java.util.ArrayList;
import java.util.List;
// No need for Map here unless you plan to index markets by ID for quick lookup

public class MarketController {
    private List<Market> markets;

    public MarketController() {
        this.markets = new ArrayList<>();
        initializeDummyMarkets(); // Populate with some data
    }

    private void initializeDummyMarkets() {
        // Create some dummy products that markets might stock.
        // Ensure these Product objects are consistent if markets are supposed to trade the *exact same* product instance.
        // For simplicity, if "Cable_0" is a type, different markets might have their "own" Cable_0 product object
        // that just shares the name and base cost. Product.equals() based on name handles this.
        Product cableType1 = new Product("Cable_0", 5.0); // Name, base production cost
        Product widgetTypeA = new Product("Widget_A", 12.0);
        Product gadgetTypeB = new Product("Gadget_B", 20.0);

        // Market_0 from screenshot (used in customer shopping)
        Market market0 = new Market("Market_0", 10000.0); // Name, initial balance
        market0.addProduct(cableType1, 50, 10.0); // Product, quantity, selling price
        market0.addProduct(widgetTypeA, 30, 25.0);
        this.markets.add(market0);

        // Market_1
        Market market1 = new Market("Market_1", 12000.0);
        // Market 1 might sell the same "type" of cable but at a different price or have a different stock.
        Product cableForMarket1 = new Product("Cable_0", 5.0); // Could be same instance as cableType1 or new
        market1.addProduct(cableForMarket1, 80, 11.0);
        market1.addProduct(gadgetTypeB, 70, 30.0);
        this.markets.add(market1);

        // Market_2
        Market market2 = new Market("Market_2", 8000.0);
        Product widgetForMarket2 = new Product("Widget_A", 12.0);
        market2.addProduct(widgetForMarket2, 60, 22.0);
        Product gadgetForMarket2 = new Product("Gadget_B", 20.0);
        market2.addProduct(gadgetForMarket2, 40, 32.0);
        this.markets.add(market2);

        // Add a few more to match the MarketGUI list screenshot if needed
        Market market3 = new Market("Market_3", 7000.0);
        market3.addProduct(new Product("Tool_X", 15.0), 20, 35.0);
        this.markets.add(market3);

        Market market4 = new Market("Market_4", 9000.0);
        market4.addProduct(new Product("SparePart_Y", 8.0), 100, 18.0);
        this.markets.add(market4);

        Market market5 = new Market("Market_5", 6000.0);
        market5.addProduct(new Product("Accessory_Z", 3.0), 150, 7.50);
        this.markets.add(market5);
    }

    public List<Market> getMarkets() {
        return new ArrayList<>(markets); // Return a copy
    }

    public void addMarket(Market market) {
        if (market == null) {
            throw new IllegalArgumentException("Market cannot be null.");
        }
        this.markets.add(market);
    }

    public Market getMarket(int index) {
        if (index < 0 || index >= markets.size()) {
            throw new IndexOutOfBoundsException("Invalid market index: " + index);
        }
        return markets.get(index);
    }

    public Market findMarketByID(String marketID) {
        for (Market market : markets) {
            // Assuming marketID is unique and obtained via market.getMarketID() or market.getCustomerID()
            if (market.getMarketID().equals(marketID)) {
                return market;
            }
        }
        return null; // Not found
    }

    /**
     * Edits an existing market's details.
     * @param marketID The ID of the market to edit.
     * @param newName The new name for the market.
     * @param newBalance The new balance for the market.
     * @throws IllegalArgumentException if market not found or inputs are invalid (handled by Market setters).
     */
    public void editMarket(String marketID, String newName, double newBalance) {
        Market marketToEdit = findMarketByID(marketID);
        if (marketToEdit == null) {
            throw new IllegalArgumentException("Market with ID " + marketID + " not found for editing.");
        }
        marketToEdit.setName(newName);
        marketToEdit.setBalance(newBalance);
    }

    // deleteMarket could be added if needed by the MarketGUI's "Edit Market" (which might imply delete too)
    // For now, only Add/Edit are explicitly shown for Markets in the PDF's Market List page.
}