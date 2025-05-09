// ================================================
// FILE: src/MarketGUI.java (Corrected and Refactored)
// ================================================
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MarketGUI extends MyWindow {
    private DefaultListModel<String> marketListModel;
    private JList<String> marketList;
    private MarketController marketController;
    private java.util.List<Factory> factories;

    public MarketGUI(java.util.List<Factory> factories, MarketController marketController) {
        super("Markets");
        this.factories = factories;
        this.marketController = marketController;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Market List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        marketListModel = new DefaultListModel<>();
        marketList = new JList<>(marketListModel);
        JScrollPane scrollPane = new JScrollPane(marketList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add New Market");
        JButton editButton = new JButton("Edit Market");
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshMarketList();

        marketList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = marketList.locationToIndex(evt.getPoint());
                    List<Market> currentMarkets = marketController.getMarkets();
                    if (index >= 0 && index < currentMarkets.size()) {
                        new MarketDetailPage(MarketGUI.this, currentMarkets.get(index), marketController, factories);
                        refreshMarketList();
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            new AddMarketDialog(MarketGUI.this, marketController);
            refreshMarketList();
        });

        editButton.addActionListener(e -> {
            int selectedIndex = marketList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a market to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Market> currentMarkets = marketController.getMarkets();
            Market selectedMarket = currentMarkets.get(selectedIndex);
            new EditMarketDialog(MarketGUI.this, selectedMarket, marketController);
            refreshMarketList();
        });

        pack();
        setLocationRelativeTo(null);

    }

    void refreshMarketList() {
        marketListModel.clear();
        List<Market> currentMarkets = marketController.getMarkets();
        Map<String, Integer> nameCount = new HashMap<>();
        for (Market m : currentMarkets) {
            nameCount.put(m.getName(), nameCount.getOrDefault(m.getName(), 0) + 1);
        }
        for (Market m : currentMarkets) {
            String display = m.getName();
            if (nameCount.get(m.getName()) > 1) {
                display += " (" + m.getMarketID() + ")";
            }
            marketListModel.addElement(display);
        }
    }

    class AddMarketDialog extends JDialog {
        private MarketController controller;

        public AddMarketDialog(Frame owner, MarketController marketController) {
            super(owner, "Add New Market", true);
            this.controller = marketController;
            setLayout(new BorderLayout());
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            JLabel nameLabel = new JLabel("Market Name:");
            JTextField nameField = new JTextField();
            JLabel balanceLabel = new JLabel("Initial Balance:");
            JTextField balanceField = new JTextField();
            formPanel.add(nameLabel); formPanel.add(nameField);
            formPanel.add(balanceLabel); formPanel.add(balanceField);
            add(formPanel, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(okButton); buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String balanceText = balanceField.getText().trim();
                double balance;
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    balance = Double.parseDouble(balanceText);
                    if (balance < 0) {
                        JOptionPane.showMessageDialog(this, "Initial balance cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for balance.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    controller.addMarket(new Market(name, balance));
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Creation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
            setSize(350, 180);
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    class EditMarketDialog extends JDialog {
        private Market marketToEdit;
        private MarketController controller;

        public EditMarketDialog(Frame owner, Market market, MarketController marketController) {
            super(owner, "Edit Market", true);
            this.marketToEdit = market;
            this.controller = marketController;
            setLayout(new BorderLayout());
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            JLabel nameLabel = new JLabel("Market Name:");
            JTextField nameField = new JTextField(marketToEdit.getName());
            JLabel balanceLabel = new JLabel("Balance:");
            JTextField balanceField = new JTextField(String.valueOf(marketToEdit.getBalance()));
            formPanel.add(nameLabel); formPanel.add(nameField);
            formPanel.add(balanceLabel); formPanel.add(balanceField);
            add(formPanel, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(okButton); buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String balanceText = balanceField.getText().trim();
                double balance;
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    balance = Double.parseDouble(balanceText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for balance.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    controller.editMarket(marketToEdit.getMarketID(), name, balance);
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
            setSize(350, 180);
            setLocationRelativeTo(owner);
            setVisible(true);
        }
    }

    class MarketDetailPage extends JDialog {
        private Market currentMarket;
        private MarketController marketController;
        private java.util.List<Factory> allFactories;
        private JLabel balanceValueLabel;
        private JComboBox<String> productCombo;
        private JLabel stockValueLabel;
        private JTextField amountField;
        private List<Object[]> availableProductsForSale;
        private JComboBox<String> marketStockProductCombo;
        private JTextField priceFieldForStock;
        private JLabel currentPriceLabelForOwnStock;

// In MarketGUI.java
// Inside class MarketDetailPage:

        public MarketDetailPage(Frame owner, Market market, MarketController marketCtrl, java.util.List<Factory> factories) {
            super(owner, "Market: " + market.getName(), true);
            this.currentMarket = market;
            this.marketController = marketCtrl;
            this.allFactories = factories;
            this.availableProductsForSale = new ArrayList<>();

            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            JLabel titleLabel = new JLabel("Market: " + currentMarket.getName());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
            topPanel.add(titleLabel, BorderLayout.WEST);
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> dispose());
            topPanel.add(backButton, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            JTabbedPane tabbedPane = new JTabbedPane();

            // --- Initialize Components for Buy Products Tab FIRST ---
            balanceValueLabel = new JLabel(String.format("%.2f", currentMarket.getBalance()));
            productCombo = new JComboBox<>();
            productCombo.setPreferredSize(new Dimension(350, 25));
            stockValueLabel = new JLabel("0"); // Initialize here
            amountField = new JTextField(10);
            JButton buyButton = new JButton("Buy Selected Product");
            buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // --- Build Buy Products Tab UI ---
            JPanel buyPanel = new JPanel();
            buyPanel.setLayout(new BoxLayout(buyPanel, BoxLayout.Y_AXIS));
            buyPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
            JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            balancePanel.add(new JLabel("Balance:"));
            balancePanel.add(balanceValueLabel); // Use initialized component
            buyPanel.add(balancePanel);
            buyPanel.add(Box.createVerticalStrut(10));
            JPanel productSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            productSelectPanel.add(new JLabel("Select Product to Buy:"));
            productSelectPanel.add(productCombo); // Use initialized component
            buyPanel.add(productSelectPanel);
            JPanel stockInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            stockInfoPanel.add(new JLabel("Seller's Stock:"));
            stockInfoPanel.add(stockValueLabel); // Use initialized component
            buyPanel.add(stockInfoPanel);
            JPanel amountToBuyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            amountToBuyPanel.add(new JLabel("Amount:"));
            amountToBuyPanel.add(amountField); // Use initialized component
            buyPanel.add(amountToBuyPanel);
            buyPanel.add(Box.createVerticalStrut(10));
            buyPanel.add(buyButton); // Use initialized component
            tabbedPane.addTab("Buy Products", buyPanel);

            // --- Initialize Components for Set Prices Tab FIRST ---
            marketStockProductCombo = new JComboBox<>();
            marketStockProductCombo.setPreferredSize(new Dimension(300, 25));
            currentPriceLabelForOwnStock = new JLabel("N/A");
            priceFieldForStock = new JTextField(10);
            JButton updatePriceButton = new JButton("Update Price for Selected Product");
            updatePriceButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // --- Build Set Prices Tab UI ---
            JPanel setPricePanel = new JPanel();
            setPricePanel.setLayout(new BoxLayout(setPricePanel, BoxLayout.Y_AXIS));
            setPricePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
            setPricePanel.add(new JLabel("Manage Prices for Your Stock:"));
            setPricePanel.add(Box.createVerticalStrut(5));
            JPanel marketStockSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            marketStockSelectPanel.add(new JLabel("Your Product:"));
            marketStockSelectPanel.add(marketStockProductCombo); // Use initialized component
            setPricePanel.add(marketStockSelectPanel);
            JPanel currentPriceInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            currentPriceInfoPanel.add(new JLabel("Current Price:"));
            currentPriceInfoPanel.add(currentPriceLabelForOwnStock); // Use initialized component
            setPricePanel.add(currentPriceInfoPanel);
            JPanel newPricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            newPricePanel.add(new JLabel("New Price:"));
            newPricePanel.add(priceFieldForStock); // Use initialized component
            setPricePanel.add(newPricePanel);
            setPricePanel.add(Box.createVerticalStrut(10));
            setPricePanel.add(updatePriceButton); // Use initialized component
            tabbedPane.addTab("Set Prices", setPricePanel);

            add(tabbedPane, BorderLayout.CENTER);

            // --- NOW Populate and Add Listeners ---
            populateBuyProductCombo(); // Safe to call now
            productCombo.addActionListener(e -> updateSellerStockInfo());
            if (productCombo.getItemCount() > 0) productCombo.setSelectedIndex(0);
            else updateSellerStockInfo(); // Ensure consistent state even if empty

            populateMarketStockCombo(); // Safe to call now
            marketStockProductCombo.addActionListener(e -> updateMarketStockPriceInfo());
            updateMarketStockPriceInfo(); // Initial call after populating

            buyButton.addActionListener(e -> {
                // ... (buyButton listener code remains the same) ...
                int selectedIdx = productCombo.getSelectedIndex();
                if (selectedIdx < 0 || selectedIdx >= availableProductsForSale.size()) {
                    JOptionPane.showMessageDialog(this, "Please select a product to buy.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String amountText = amountField.getText().trim();
                int amount;
                try {
                    amount = Integer.parseInt(amountText);
                    if (amount <= 0) throw new NumberFormatException("Amount must be positive.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Object[] prodInfo = availableProductsForSale.get(selectedIdx);
                Product productToBuy = (Product) prodInfo[0];
                double priceFromSeller = (double) prodInfo[1];
                Customer seller = (Customer) prodInfo[3];

                try {
                    currentMarket.buyProduct(productToBuy, amount, priceFromSeller, seller);
                    balanceValueLabel.setText(String.format("%.2f", currentMarket.getBalance()));
                    JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh UI elements after purchase
                    int previousBuySelection = productCombo.getSelectedIndex();
                    populateBuyProductCombo();
                    if(productCombo.getItemCount() > 0) {
                        if(previousBuySelection < productCombo.getItemCount() && previousBuySelection != -1) {
                            productCombo.setSelectedIndex(previousBuySelection);
                        } else {
                            productCombo.setSelectedIndex(0);
                        }
                    }
                    updateSellerStockInfo(); // Update based on new selection or empty list

                    amountField.setText("");

                    int previousStockSelection = marketStockProductCombo.getSelectedIndex();
                    populateMarketStockCombo(); // Also refresh own stock combo
                    if(marketStockProductCombo.getItemCount() > 0) {
                        if(previousStockSelection < marketStockProductCombo.getItemCount() && previousStockSelection != -1) {
                            marketStockProductCombo.setSelectedIndex(previousStockSelection);
                        } else {
                            marketStockProductCombo.setSelectedIndex(0);
                        }
                    }
                    updateMarketStockPriceInfo(); // Update based on new selection or empty list


                } catch (IllegalStateException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error during purchase: " + ex.getMessage(), "Purchase Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            updatePriceButton.addActionListener(e -> {
                // ... (updatePriceButton listener code remains the same) ...
                int selectedIdx = marketStockProductCombo.getSelectedIndex();
                if (selectedIdx == -1 || marketStockProductCombo.getSelectedItem().toString().startsWith("No products")) {
                    JOptionPane.showMessageDialog(this, "Please select one of your products to set its price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String newPriceText = priceFieldForStock.getText().trim();
                double newPrice;
                try {
                    newPrice = Double.parseDouble(newPriceText);
                    if (newPrice < 0) throw new NumberFormatException("Price cannot be negative.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid non-negative price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedItemStr = (String) marketStockProductCombo.getSelectedItem();
                Product productToUpdate = findProductInMarketInventoryByName(selectedItemStr.split(" \\|")[0].trim());

                if (productToUpdate != null) {
                    try {
                        currentMarket.setProductPrice(productToUpdate, newPrice);
                        JOptionPane.showMessageDialog(this, "Price updated successfully for " + productToUpdate.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateMarketStockPriceInfo();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Price Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Selected product not found in your inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            pack();
            setMinimumSize(new Dimension(500, 350));
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        // ... (rest of MarketDetailPage methods like findProductInMarketInventoryByName,
        //      populateBuyProductCombo, updateSellerStockInfo, populateMarketStockCombo,
        //      updateMarketStockPriceInfo remain the same as in the previous "Corrected and Refactored" version) ...
        // Make sure these methods are correctly placed within the MarketDetailPage class body.
        // The code for these methods was provided in the previous response and should be correct.
        // I will re-paste them here for completeness of MarketDetailPage:

        private Product findProductInMarketInventoryByName(String name) {
            for(Product p : currentMarket.getInventory().keySet()){
                if(p.getName().equals(name)) return p;
            }
            return null;
        }

        private void updateMarketStockPriceInfo() {
            int selectedIdx = marketStockProductCombo.getSelectedIndex();
            if (selectedIdx != -1 && marketStockProductCombo.getItemCount() > 0 &&
                    marketStockProductCombo.getSelectedItem() != null && // Add null check forgetSelectedItem()
                    !marketStockProductCombo.getSelectedItem().toString().startsWith("No products")) {
                String selectedItemStr = (String) marketStockProductCombo.getSelectedItem();
                Product selectedProd = findProductInMarketInventoryByName(selectedItemStr.split(" \\|")[0].trim());
                if(selectedProd != null) {
                    double currentPrice = currentMarket.getProductPrice(selectedProd);
                    currentPriceLabelForOwnStock.setText(String.format("%.2f", currentPrice));
                    priceFieldForStock.setText(String.format("%.2f", currentPrice));
                    priceFieldForStock.setEnabled(true);
                } else {
                    currentPriceLabelForOwnStock.setText("N/A");
                    priceFieldForStock.setText("");
                    priceFieldForStock.setEnabled(false);
                }
            } else {
                currentPriceLabelForOwnStock.setText("N/A");
                priceFieldForStock.setText("");
                priceFieldForStock.setEnabled(false);
            }
        }

        private void populateBuyProductCombo() {
            availableProductsForSale.clear();
            Object currentSelection = productCombo.getSelectedItem();
            productCombo.removeAllItems();

            for (Market otherMarket : marketController.getMarkets()) {
                if (otherMarket.equals(currentMarket)) continue;
                for (Map.Entry<Product, Integer> entry : otherMarket.getInventory().entrySet()) {
                    Product p = entry.getKey();
                    int stock = entry.getValue();
                    if (stock > 0) {
                        double price = otherMarket.getProductPrice(p);
                        availableProductsForSale.add(new Object[]{p, price, stock, otherMarket});
                        productCombo.addItem(String.format("%s | %s (Price: %.2f, Stock: %d)", otherMarket.getName(), p.getName(), price, stock));
                    }
                }
            }
            for (Factory factory : allFactories) {
                for (ProductDesign design : factory.getDesigns()) {
                    Product factoryProduct = design.getProduct();
                    int factoryStock = factory.getProducts().getOrDefault(factoryProduct.getName(), 0);
                    if (factoryStock > 0) {
                        double factorySellPrice = design.getProductionCost() * 1.2;
                        if (factorySellPrice < 0 && design.getProductionCost() >= 0) factorySellPrice = design.getProductionCost();
                        else if (factorySellPrice < 0) factorySellPrice = 0;


                        availableProductsForSale.add(new Object[]{factoryProduct, factorySellPrice, factoryStock, factory});
                        productCombo.addItem(String.format("%s (Factory) | %s (Price: %.2f, Stock: %d)", factory.getName(), factoryProduct.getName(), factorySellPrice, factoryStock));
                    }
                }
            }
            if (currentSelection != null) {
                for (int i = 0; i < productCombo.getItemCount(); i++) {
                    if (productCombo.getItemAt(i).equals(currentSelection.toString())) {
                        productCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (productCombo.getSelectedIndex() == -1 && productCombo.getItemCount() > 0) {
                productCombo.setSelectedIndex(0);
            }
            // updateSellerStockInfo(); // This was called here, but it's better called by the action listener or after setting index
        }

        private void updateSellerStockInfo() {
            int selectedIdx = productCombo.getSelectedIndex();
            if (selectedIdx >= 0 && selectedIdx < availableProductsForSale.size()) {
                stockValueLabel.setText(String.valueOf(availableProductsForSale.get(selectedIdx)[2]));
            } else {
                stockValueLabel.setText("N/A");
            }
        }

        private void populateMarketStockCombo() {
            marketStockProductCombo.removeAllItems();
            Map<Product, Integer> stock = currentMarket.getInventory();
            if (stock.isEmpty()) {
                marketStockProductCombo.addItem("No products in your stock");
                marketStockProductCombo.setEnabled(false);
                // priceFieldForStock.setEnabled(false); // This is handled by updateMarketStockPriceInfo
                // currentPriceLabelForOwnStock.setText("N/A");
            } else {
                marketStockProductCombo.setEnabled(true);
                // priceFieldForStock.setEnabled(true); // This is handled by updateMarketStockPriceInfo
                for (Map.Entry<Product, Integer> entry : stock.entrySet()) {
                    marketStockProductCombo.addItem(entry.getKey().getName() + " | Amount: " + entry.getValue() + " | Cost: " + String.format("%.2f",entry.getKey().getProductionCost()));
                }
                if(marketStockProductCombo.getItemCount() > 0) {
                    marketStockProductCombo.setSelectedIndex(0);
                }
            }
            updateMarketStockPriceInfo();
        }
    } // End of MarketDetailPage
        }

