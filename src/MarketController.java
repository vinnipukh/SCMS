import java.util.ArrayList;
import java.util.List;

public class MarketController {
    private List<Market> markets;

    public MarketController() {
        this.markets = new ArrayList<>();
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
            if (market.getMarketID().equals(marketID)) {
                return market;
            }
        }
        return null;
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

}