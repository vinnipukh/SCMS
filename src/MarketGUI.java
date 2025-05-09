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

// ================================================
// FILE: src/MarketGUI.java (MarketDetailPage visually updated)
// ================================================
// ... (Keep MarketGUI main class, AddMarketDialog, EditMarketDialog as they were from the "Corrected and Refactored" version) ...

    // --- Inner Class MarketDetailPage (Visually Updated to match PDF Page 9) ---
    class MarketDetailPage extends JDialog {
        private Market currentMarket;
        private MarketController marketController;
        private java.util.List<Factory> allFactories;

        private JLabel balanceValueLabel;
        // For Buying Products
        private JComboBox<String> productToBuyCombo;
        private JLabel sellerStockQtyLabel;
        private JTextField amountToBuyField;
        // For Setting Prices of Own Stock
        private JComboBox<String> ownStockProductCombo; // To select product from own stock
        private JTextField priceForOwnStockField;
        // Helper list for product buying
        private List<Object[]> availableProductsForSale; // [Product, price, current stock, sellerEntity (Market/Factory)]


        public MarketDetailPage(Frame owner, Market market, MarketController marketCtrl, java.util.List<Factory> factories) {
            super(owner, "Market: " + market.getName(), true);
            this.currentMarket = market;
            this.marketController = marketCtrl;
            this.allFactories = factories;
            this.availableProductsForSale = new ArrayList<>();

            setLayout(new BorderLayout(10,10));
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // --- Top Panel: Title and Back Button ---
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Added padding
            JLabel titleLabel = new JLabel("Market: " + currentMarket.getName());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly larger font
            topPanel.add(titleLabel, BorderLayout.WEST);
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> dispose());
            topPanel.add(backButton, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            // --- Main Content Panel ---
            JPanel mainContentPanel = new JPanel();
            mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
            mainContentPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // Padding

            Dimension labelDim = new Dimension(120, 25); // For consistent label width
            Dimension fieldDim = new Dimension(180, 25); // For consistent field width

            // Balance Display
            JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel balanceLabel = new JLabel("Balance:");
            balanceLabel.setPreferredSize(labelDim);
            balancePanel.add(balanceLabel);
            balanceValueLabel = new JLabel(String.format("%.2f", currentMarket.getBalance()));
            balanceValueLabel.setPreferredSize(fieldDim);
            balancePanel.add(balanceValueLabel);
            mainContentPanel.add(balancePanel);

            // --- Buying Section ---
            // Select Product to Buy
            JPanel selectBuyProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel selectBuyLabel = new JLabel("Select Product:");
            selectBuyLabel.setPreferredSize(labelDim);
            selectBuyProductPanel.add(selectBuyLabel);
            productToBuyCombo = new JComboBox<>();
            productToBuyCombo.setPreferredSize(fieldDim);
            selectBuyProductPanel.add(productToBuyCombo);
            mainContentPanel.add(selectBuyProductPanel);

            // Seller's Stock Quantity
            JPanel sellerStockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel sellerStockLabelText = new JLabel("Stock Quantity:");
            sellerStockLabelText.setPreferredSize(labelDim);
            sellerStockPanel.add(sellerStockLabelText);
            sellerStockQtyLabel = new JLabel("0");
            sellerStockQtyLabel.setPreferredSize(fieldDim);
            sellerStockPanel.add(sellerStockQtyLabel);
            mainContentPanel.add(sellerStockPanel);

            // Amount to Buy
            JPanel amountBuyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel amountBuyLabel = new JLabel("Amount:");
            amountBuyLabel.setPreferredSize(labelDim);
            amountBuyPanel.add(amountBuyLabel);
            amountToBuyField = new JTextField();
            amountToBuyField.setPreferredSize(fieldDim);
            amountBuyPanel.add(amountToBuyField);
            mainContentPanel.add(amountBuyPanel);

            // Buy Button
            JPanel buyButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Centered
            JButton buyButton = new JButton("Buy");
            buyButton.setPreferredSize(new Dimension(100,30));
            buyButtonPanel.add(buyButton);
            mainContentPanel.add(buyButtonPanel);

            mainContentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer

            // --- Setting Price for Own Stock Section ---
            // Own Stock Product Selection (for setting price) - PDF doesn't show this selection, assumes one product or implicit selection
            // To match PDF closely for "Price (for stock)" we'd need a way to select which owned product.
            // Let's add a JComboBox for owned products if there are multiple.
            // If the intent of "Price (for stock)" is to set a price for the *last bought* or *a specific product*,
            // the UI needs to reflect that. The PDF is ambiguous here if market owns multiple items.
            // For now, assuming we allow setting price for any item in own stock.
            JPanel selectOwnStockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel selectOwnStockLabel = new JLabel("Your Product:"); // Label for clarity
            selectOwnStockLabel.setPreferredSize(labelDim);
            selectOwnStockPanel.add(selectOwnStockLabel);
            ownStockProductCombo = new JComboBox<>();
            ownStockProductCombo.setPreferredSize(fieldDim);
            selectOwnStockPanel.add(ownStockProductCombo);
            mainContentPanel.add(selectOwnStockPanel);


            // Price (for stock)
            JPanel priceSetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            JLabel priceSetLabel = new JLabel("Price (for stock):");
            priceSetLabel.setPreferredSize(labelDim);
            priceSetPanel.add(priceSetLabel);
            priceForOwnStockField = new JTextField();
            priceForOwnStockField.setPreferredSize(fieldDim);
            priceSetPanel.add(priceForOwnStockField);
            mainContentPanel.add(priceSetPanel);

            // Update Price Button
            JPanel updatePriceButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Centered
            JButton updatePriceButton = new JButton("Update Price");
            updatePriceButton.setPreferredSize(new Dimension(120,30));
            updatePriceButtonPanel.add(updatePriceButton);
            mainContentPanel.add(updatePriceButtonPanel);

            add(mainContentPanel, BorderLayout.CENTER);

            // --- Initialize and Populate Combos, Add Listeners ---
            populateProductToBuyCombo();
            productToBuyCombo.addActionListener(e -> updateSellerStockQtyLabel());
            if (productToBuyCombo.getItemCount() > 0) productToBuyCombo.setSelectedIndex(0);
            else updateSellerStockQtyLabel(); // Handle empty case

            populateOwnStockProductCombo();
            ownStockProductCombo.addActionListener(e -> updatePriceForOwnStockField());
            if (ownStockProductCombo.getItemCount() > 0) ownStockProductCombo.setSelectedIndex(0);
            else updatePriceForOwnStockField(); // Handle empty


            buyButton.addActionListener(e -> {
                int selectedIdx = productToBuyCombo.getSelectedIndex();
                if (selectedIdx < 0 || selectedIdx >= availableProductsForSale.size()) {
                    JOptionPane.showMessageDialog(this, "Please select a product to buy.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String amountText = amountToBuyField.getText().trim();
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

                    int previousBuySelection = productToBuyCombo.getSelectedIndex();
                    populateProductToBuyCombo(); // Refresh list of products to buy
                    if(productToBuyCombo.getItemCount() > 0) {
                        if(previousBuySelection < productToBuyCombo.getItemCount() && previousBuySelection != -1) productToBuyCombo.setSelectedIndex(previousBuySelection);
                        else productToBuyCombo.setSelectedIndex(0);
                    }
                    updateSellerStockQtyLabel();
                    amountToBuyField.setText("");

                    // Buying might add new product types to own stock, so refresh that combo too
                    int previousOwnStockSelection = ownStockProductCombo.getSelectedIndex();
                    populateOwnStockProductCombo();
                    if(ownStockProductCombo.getItemCount() > 0){
                        if(previousOwnStockSelection < ownStockProductCombo.getItemCount() && previousOwnStockSelection != -1) ownStockProductCombo.setSelectedIndex(previousOwnStockSelection);
                        else ownStockProductCombo.setSelectedIndex(0);
                    }
                    updatePriceForOwnStockField();

                } catch (IllegalStateException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error during purchase: " + ex.getMessage(), "Purchase Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            updatePriceButton.addActionListener(e -> {
                int selectedOwnStockIdx = ownStockProductCombo.getSelectedIndex();
                if (selectedOwnStockIdx == -1 || ownStockProductCombo.getSelectedItem().toString().startsWith("No items")) {
                    JOptionPane.showMessageDialog(this, "Please select one of your products to set its price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String newPriceText = priceForOwnStockField.getText().trim();
                double newPrice;
                try {
                    newPrice = Double.parseDouble(newPriceText);
                    if (newPrice < 0) throw new NumberFormatException("Price cannot be negative.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid non-negative price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get the actual Product object from the combo box selection
                String selectedItemName = ((String) ownStockProductCombo.getSelectedItem()).split(" \\(")[0].trim(); // Get name part
                Product productToUpdate = findProductInMarketInventoryByName(selectedItemName);


                if (productToUpdate != null) {
                    try {
                        currentMarket.setProductPrice(productToUpdate, newPrice);
                        JOptionPane.showMessageDialog(this, "Price updated successfully for " + productToUpdate.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                        // No need to repopulate, just ensure field reflects the new price if user changes selection
                        updatePriceForOwnStockField();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Price Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Selected product '" + selectedItemName + "' not found in your inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            pack();
            setMinimumSize(new Dimension(480, 420)); // Adjusted minimum size
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        private Product findProductInMarketInventoryByName(String name) {
            for(Product p : currentMarket.getInventory().keySet()){
                if(p.getName().equals(name)) return p;
            }
            return null;
        }

        private void populateProductToBuyCombo() {
            availableProductsForSale.clear();
            Object currentSelection = productToBuyCombo.getSelectedItem();
            productToBuyCombo.removeAllItems();

            for (Market otherMarket : marketController.getMarkets()) {
                if (otherMarket.equals(currentMarket)) continue;
                for (Map.Entry<Product, Integer> entry : otherMarket.getInventory().entrySet()) {
                    Product p = entry.getKey();
                    int stock = entry.getValue();
                    if (stock > 0) {
                        double price = otherMarket.getProductPrice(p);
                        // Format: "Factory_0 | Copper"
                        availableProductsForSale.add(new Object[]{p, price, stock, otherMarket});
                        productToBuyCombo.addItem(String.format("%s | %s", otherMarket.getName(), p.getName()));
                    }
                }
            }
            for (Factory factory : allFactories) {
                for (ProductDesign design : factory.getDesigns()) {
                    Product factoryProduct = design.getProduct();
                    int factoryStock = factory.getProducts().getOrDefault(factoryProduct.getName(), 0);
                    if (factoryStock > 0) {
                        double factorySellPrice = design.getProductionCost() * 1.2;
                        if (factorySellPrice < 0 && design.getProductionCost() >=0) factorySellPrice = design.getProductionCost();
                        else if (factorySellPrice < 0) factorySellPrice = 0;

                        availableProductsForSale.add(new Object[]{factoryProduct, factorySellPrice, factoryStock, factory});
                        productToBuyCombo.addItem(String.format("%s | %s", factory.getName(), factoryProduct.getName())); // Match PDF "Factory_0 | Copper"
                    }
                }
            }
            if (currentSelection != null) {
                for (int i = 0; i < productToBuyCombo.getItemCount(); i++) {
                    if (productToBuyCombo.getItemAt(i).equals(currentSelection.toString())) {
                        productToBuyCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (productToBuyCombo.getSelectedIndex() == -1 && productToBuyCombo.getItemCount() > 0) {
                productToBuyCombo.setSelectedIndex(0);
            }
        }

        private void updateSellerStockQtyLabel() {
            int selectedIdx = productToBuyCombo.getSelectedIndex();
            if (selectedIdx >= 0 && selectedIdx < availableProductsForSale.size()) {
                // availableProductsForSale stores: [Product, price, stock, sellerEntity]
                sellerStockQtyLabel.setText(String.valueOf(availableProductsForSale.get(selectedIdx)[2])); // Index 2 is stock
            } else {
                sellerStockQtyLabel.setText("0"); // Match PDF which shows 0 when selection implies no stock info
            }
        }

        private void populateOwnStockProductCombo() {
            ownStockProductCombo.removeAllItems();
            Map<Product, Integer> stock = currentMarket.getInventory();
            if (stock.isEmpty()) {
                ownStockProductCombo.addItem("No items in your stock");
                ownStockProductCombo.setEnabled(false);
            } else {
                ownStockProductCombo.setEnabled(true);
                for (Map.Entry<Product, Integer> entry : stock.entrySet()) {
                    // Display: "ProductName (Qty: X, Cost: Y.YY, Price: Z.ZZ)"
                    Product p = entry.getKey();
                    ownStockProductCombo.addItem(String.format("%s (Qty: %d)", p.getName(), entry.getValue()));
                }
                if(ownStockProductCombo.getItemCount() > 0) {
                    ownStockProductCombo.setSelectedIndex(0);
                }
            }
        }

        private void updatePriceForOwnStockField() {
            int selectedIdx = ownStockProductCombo.getSelectedIndex();
            if (selectedIdx != -1 && ownStockProductCombo.getSelectedItem() != null &&
                    !ownStockProductCombo.getSelectedItem().toString().startsWith("No items")) {
                String selectedItemName = ((String) ownStockProductCombo.getSelectedItem()).split(" \\(")[0].trim();
                Product selectedProd = findProductInMarketInventoryByName(selectedItemName);
                if(selectedProd != null) {
                    priceForOwnStockField.setText(String.format("%.2f", currentMarket.getProductPrice(selectedProd)));
                    priceForOwnStockField.setEnabled(true);
                } else {
                    priceForOwnStockField.setText("");
                    priceForOwnStockField.setEnabled(false);
                }
            } else {
                priceForOwnStockField.setText("");
                priceForOwnStockField.setEnabled(false);
            }
        }
    } // End of MarketDetailPage
// ... (Rest of MarketGUI: AddMarketDialog, EditMarketDialog remain the same)
        }

