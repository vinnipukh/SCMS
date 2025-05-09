import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MarketGUI extends MyWindow {
    DefaultListModel<String> marketListModel;
    JList<String> marketList;
    java.util.List<Market> markets;
    java.util.List<Factory> factories;

    public MarketGUI(java.util.List<Factory> factories) {
        super("Markets");
        this.factories = factories;
        setLayout(new BorderLayout());
        markets = new ArrayList<>();
        // Dummy data for demonstration
        for (int i = 0; i < 6; i++) {
            markets.add(new Market("Market_" + i, 5000.0));
        }

        // Top panel with title and Back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Market List");
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // List of markets
        marketListModel = new DefaultListModel<>();
        marketList = new JList<>(marketListModel);
        JScrollPane scrollPane = new JScrollPane(marketList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Add/Edit buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add New Market");
        JButton editButton = new JButton("Edit Market");
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshMarketList();

        // Double-click to open detail page
        marketList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = marketList.locationToIndex(evt.getPoint());
                    if (index >= 0 && index < markets.size()) {
                        new MarketDetailPage(markets.get(index), markets, factories);
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            new AddMarketDialog(MarketGUI.this);
            refreshMarketList();
        });

        editButton.addActionListener(e -> {
            int selectedIndex = marketList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a market to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new EditMarketDialog(MarketGUI.this, markets.get(selectedIndex));
            refreshMarketList();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void refreshMarketList() {
        marketListModel.clear();
        Map<String, Integer> nameCount = new HashMap<>();
        for (Market m : markets) {
            nameCount.put(m.getName(), nameCount.getOrDefault(m.getName(), 0) + 1);
        }
        for (Market m : markets) {
            String display = m.getName();
            if (nameCount.get(m.getName()) > 1) {
                display += " (" + m.getCustomerID() + ")";
            }
            marketListModel.addElement(display);
        }
    }
}

// Market detail page
class MarketDetailPage extends JDialog {
    public MarketDetailPage(Market market, java.util.List<Market> allMarkets, java.util.List<Factory> allFactories) {
        super((JFrame) null, "Market: " + market.getName(), true);
        setLayout(new BorderLayout());
        
        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Market: " + market.getName());
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Main content panel using BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Balance panel
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.add(new JLabel("Balance:"));
        JLabel balanceValueLabel = new JLabel(String.valueOf(market.getBalance()));
        balancePanel.add(balanceValueLabel);
        mainPanel.add(balancePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Product selection panel
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productPanel.add(new JLabel("Select Product:"));
        JComboBox<String> productCombo = new JComboBox<>();
        java.util.List<Object[]> availableProducts = new ArrayList<>(); // [Product, price, amount, seller]
        
        // Add products from factories
        for (Factory factory : allFactories) {
            for (Map.Entry<String, Integer> entry : factory.getProducts().entrySet()) {
                if (entry.getValue() > 0) {
                    String entryStr = factory.getName() + " | " + entry.getKey() + " | Price: " + getProductCost(entry.getKey(), factory) + " | Amount: " + entry.getValue() + " | Seller: " + factory.getName();
                    productCombo.addItem(entryStr);
                    availableProducts.add(new Object[]{entry.getKey(), getProductCost(entry.getKey(), factory), entry.getValue(), factory});
                }
            }
        }
        
        // Add products from other markets
        for (Market m : allMarkets) {
            if (m == market) continue;
            for (Map.Entry<Product, Integer> entry : m.getInventory().entrySet()) {
                if (entry.getValue() > 0) {
                    String entryStr = m.getName() + " | " + entry.getKey().getName() + " | Price: " + m.getProductPrice(entry.getKey()) + " | Amount: " + entry.getValue() + " | Seller: " + m.getName();
                    productCombo.addItem(entryStr);
                    availableProducts.add(new Object[]{entry.getKey(), m.getProductPrice(entry.getKey()), entry.getValue(), m});
                }
            }
        }
        productCombo.setPreferredSize(new Dimension(300, 25));
        productPanel.add(productCombo);
        mainPanel.add(productPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Stock quantity panel
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockPanel.add(new JLabel("Stock Quantity:"));
        JLabel stockValueLabel = new JLabel("0");
        stockPanel.add(stockValueLabel);
        mainPanel.add(stockPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Amount panel
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField(10);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Buy button panel
        JPanel buyButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton buyButton = new JButton("Buy");
        buyButtonPanel.add(buyButton);
        mainPanel.add(buyButtonPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Price panel
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.add(new JLabel("Price (for stock):"));
        JTextField priceField = new JTextField(10);
        pricePanel.add(priceField);
        mainPanel.add(pricePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Update price button panel
        JPanel updatePricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton setPriceButton = new JButton("Update Price");
        updatePricePanel.add(setPriceButton);
        mainPanel.add(updatePricePanel);

        // Add main panel to dialog
        add(mainPanel, BorderLayout.CENTER);

        // Update stock quantity when product is selected
        productCombo.addActionListener(e -> {
            int idx = productCombo.getSelectedIndex();
            if (idx >= 0 && idx < availableProducts.size()) {
                int stock = (int) availableProducts.get(idx)[2];
                stockValueLabel.setText(String.valueOf(stock));
            } else {
                stockValueLabel.setText("0");
            }
        });
        if (productCombo.getItemCount() > 0) {
            productCombo.setSelectedIndex(0);
        }

        // Buy button logic
        buyButton.addActionListener(e -> {
            int selectedIdx = productCombo.getSelectedIndex();
            if (selectedIdx == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to buy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String amountText = amountField.getText().trim();
            int amount;
            try {
                amount = Integer.parseInt(amountText);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Object[] prodInfo = availableProducts.get(selectedIdx);
            Object productObj = prodInfo[0];
            double price = (double) prodInfo[1];
            int available = (int) prodInfo[2];
            Object seller = prodInfo[3];
            if (amount > available) {
                JOptionPane.showMessageDialog(this, "Seller does not have enough stock.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double totalCost = amount * price;
            if (market.getBalance() < totalCost) {
                JOptionPane.showMessageDialog(this, "You don't have enough funds to buy this product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (seller instanceof Factory) {
                    // Find the Product object by name from allFactories
                    String productName = (String) productObj;
                    Product product = null;
                    for (Factory f : allFactories) {
                        for (ProductDesign design : f.getDesigns()) {
                            if (design.getProduct().getName().equals(productName)) {
                                product = design.getProduct();
                                break;
                            }
                        }
                        if (product != null) break;
                    }
                    if (product == null) {
                        product = new Product(productName, price);
                    }
                    market.buyProduct(product, amount, price, (Factory) seller);
                } else if (seller instanceof Market) {
                    market.buyProduct((Product) productObj, amount, price, (Market) seller);
                }
                balanceValueLabel.setText(String.valueOf(market.getBalance()));
                stockValueLabel.setText(String.valueOf(available - amount));
                JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Set price button logic
        setPriceButton.addActionListener(e -> {
            // Show a dialog with a list of products in storage (name, amount, cost)
            new MarketInventoryDialog(market);
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper to get product cost from factory (implement as needed)
    private double getProductCost(String productName, Factory factory) {
        // TODO: Implement this to return the cost of the product from the factory
        return 0.0;
    }
}

// Dialog for shopping (buying products)
class ShopDialog extends JDialog {
    public ShopDialog(Market market, java.util.List<Market> allMarkets, JLabel balanceLabel) {
        super((JFrame) null, "Shop - " + market.getName(), true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setSize(400, 250);
        setModal(true);

        add(Box.createVerticalStrut(20));
        JComboBox<String> productCombo = new JComboBox<>();
        java.util.List<Object[]> availableProducts = new ArrayList<>(); // [Product, price, amount, seller]
        for (Market m : allMarkets) {
            if (m == market) continue;
            for (Map.Entry<Product, Integer> entry : m.getInventory().entrySet()) {
                if (entry.getValue() > 0) {
                    String entryStr = entry.getKey().getName() + " | Price: " + m.getProductPrice(entry.getKey()) + " | Amount: " + entry.getValue() + " | Seller: " + m.getName();
                    productCombo.addItem(entryStr);
                    availableProducts.add(new Object[]{entry.getKey(), m.getProductPrice(entry.getKey()), entry.getValue(), m});
                }
            }
        }
        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        add(productCombo);
        add(Box.createVerticalStrut(8));
        JLabel amountLabel = new JLabel("Amount:");
        add(amountLabel);
        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        add(amountField);
        add(Box.createVerticalStrut(8));
        JButton buyButton = new JButton("Buy");
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(buyButton);
        add(Box.createVerticalStrut(20));
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        add(closeButton);

        buyButton.addActionListener(e -> {
            int selectedIdx = productCombo.getSelectedIndex();
            if (selectedIdx == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to buy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String amountText = amountField.getText().trim();
            int amount;
            try {
                amount = Integer.parseInt(amountText);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Object[] prodInfo = availableProducts.get(selectedIdx);
            Product product = (Product) prodInfo[0];
            double price = (double) prodInfo[1];
            int available = (int) prodInfo[2];
            Market seller = (Market) prodInfo[3];
            if (amount > available) {
                JOptionPane.showMessageDialog(this, "Seller does not have enough stock.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double totalCost = amount * price;
            if (market.getBalance() < totalCost) {
                JOptionPane.showMessageDialog(this, "You don't have enough funds to buy this product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                market.buyProduct(product, amount, price, (Customer) seller);
                balanceLabel.setText("Balance:  " + market.getBalance());
                JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        closeButton.addActionListener(e -> dispose());
        setVisible(true);
    }
}

// Dialog for viewing and updating inventory for Market
class MarketInventoryDialog extends JDialog {
    public MarketInventoryDialog(Market market) {
        super((JFrame) null, "Inventory - " + market.getName(), true);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultListModel<String> inventoryModel = new DefaultListModel<>();
        java.util.List<Product> products = new ArrayList<>(market.getInventory().keySet());
        for (Product p : products) {
            int amt = market.getInventory().get(p);
            double price = market.getProductPrice(p);
            inventoryModel.addElement(p.getName() + " | Amount: " + amt + " | Price: " + price);
        }
        JList<String> inventoryList = new JList<>(inventoryModel);
        JScrollPane scrollPane = new JScrollPane(inventoryList);
        panel.add(scrollPane);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);
        add(panel, BorderLayout.CENTER);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Dialog for adding a new market
class AddMarketDialog extends JDialog {
    public AddMarketDialog(MarketGUI parent) {
        super((JFrame) null, "Add New Market", true);
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
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
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
                if (balance < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for balance.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parent.markets.add(new Market(name, balance));
            parent.refreshMarketList();
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 180);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Dialog for editing a market
class EditMarketDialog extends JDialog {
    public EditMarketDialog(MarketGUI parent, Market market) {
        super((JFrame) null, "Edit Market", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel nameLabel = new JLabel("Market Name:");
        JTextField nameField = new JTextField(market.getName());
        JLabel balanceLabel = new JLabel("Balance:");
        JTextField balanceField = new JTextField(String.valueOf(market.getBalance()));
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(balanceLabel); formPanel.add(balanceField);
        add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
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
                if (balance < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for balance.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            market.setName(name);
            market.setBalance(balance);
            parent.refreshMarketList();
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 180);
        setLocationRelativeTo(null);
        setVisible(true);
    }
} 