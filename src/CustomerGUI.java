
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class CustomerGUI extends JFrame {

    private DefaultListModel<String> customerListModel;
    private JList<String> customerList;
    private CustomerController customerController;
    private MarketController marketController;

    public CustomerGUI(CustomerController customerController, MarketController marketController) {
        super("Customers");
        this.customerController = customerController;
        this.marketController = marketController;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(500, 400));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Customer List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(customerList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add New Customer");
        JButton editButton = new JButton("Edit Customer");
        JButton deleteButton = new JButton("Delete Customer");
        JButton viewButton = new JButton("View Details");

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(viewButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCustomerList();

        addButton.addActionListener(e -> {
            new AddCustomerDialog(this);
            refreshCustomerList();
        });

        editButton.addActionListener(e -> {
            int selectedIndex = customerList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Customers selectedCustomer = customerController.getCustomers().get(selectedIndex);
            new EditCustomerDialog(this, selectedCustomer);
            refreshCustomerList();
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = customerList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Customers selectedCustomer = customerController.getCustomers().get(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + selectedCustomer.getName() + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    customerController.deleteCustomer(selectedCustomer.getCustomerID());
                    refreshCustomerList();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewButton.addActionListener(e -> openCustomerDetail());

        customerList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openCustomerDetail();
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openCustomerDetail() {
        int selectedIndex = customerList.getSelectedIndex();
        if (selectedIndex != -1) {
            Customers selectedCustomer = customerController.getCustomers().get(selectedIndex);
            new CustomerDetailPage(this, selectedCustomer);
            refreshCustomerList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshCustomerList() {
        customerListModel.clear();
        List<Customers> customers = customerController.getCustomers();
        Map<String, Integer> nameCount = new HashMap<>();
        for (Customers c : customers) {
            nameCount.put(c.getName(), nameCount.getOrDefault(c.getName(), 0) + 1);
        }
        for (Customers customer : customers) {
            String displayName = customer.getName();
            if (nameCount.get(customer.getName()) > 1) {
                displayName += " (ID: " + customer.getCustomerID() + ")";
            }
            customerListModel.addElement(displayName);
        }
    }

    class AddCustomerDialog extends JDialog {
        private JTextField nameField;
        private JTextField balanceField;

        public AddCustomerDialog(Frame owner) {
            super(owner, "Add New Customer", true);
            setLayout(new BorderLayout(10, 10));
            setSize(350, 200);
            setLocationRelativeTo(owner);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            formPanel.add(new JLabel("Customer Name:"));
            nameField = new JTextField();
            formPanel.add(nameField);
            formPanel.add(new JLabel("Initial Balance:"));
            balanceField = new JTextField();
            formPanel.add(balanceField);
            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Customer name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double balance;
                    try {
                        balance = Double.parseDouble(balanceField.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Initial balance must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        balanceField.requestFocus();
                        return;
                    }
                    if (balance < 0) {
                        JOptionPane.showMessageDialog(this, "Initial balance cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        balanceField.requestFocus();
                        return;
                    }
                    customerController.addCustomer(new Customers(name, balance));
                    CustomerGUI.this.refreshCustomerList();
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
            setVisible(true);
        }
    }

    class EditCustomerDialog extends JDialog {
        private JTextField nameField;
        private JTextField balanceField;
        private Customers customerToEdit;

        public EditCustomerDialog(Frame owner, Customers customer) {
            super(owner, "Edit Customer", true);
            this.customerToEdit = customer;
            setLayout(new BorderLayout(10, 10));
            setSize(350, 200);
            setLocationRelativeTo(owner);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            formPanel.add(new JLabel("Customer Name:"));
            nameField = new JTextField(customerToEdit.getName());
            formPanel.add(nameField);
            formPanel.add(new JLabel("Balance:"));
            balanceField = new JTextField(String.valueOf(customerToEdit.getBalance()));
            formPanel.add(balanceField);
            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(e -> {
                try {
                    String newName = nameField.getText().trim();
                    if (newName.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Customer name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double newBalance;
                    try {
                        newBalance = Double.parseDouble(balanceField.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Balance must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        balanceField.requestFocus();
                        return;
                    }
                    customerController.editCustomer(customerToEdit.getCustomerID(), newName, newBalance);
                    CustomerGUI.this.refreshCustomerList();
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelButton.addActionListener(e -> dispose());
            setVisible(true);
        }
    }

    class CustomerDetailPage extends JDialog {
        private Customers customer;
        private JLabel balanceValueLabel;

        public CustomerDetailPage(Frame owner, Customers customer) {
            super(owner, "Customer: " + customer.getName(), true);
            this.customer = customer;
            setLayout(new BorderLayout(10, 10));
            setSize(400, 250);
            setLocationRelativeTo(owner);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            JLabel titleLabel = new JLabel("Customer: " + customer.getName());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            topPanel.add(titleLabel, BorderLayout.WEST);
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> dispose());
            topPanel.add(backButton, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.add(new JLabel("Name:"));
            namePanel.add(new JLabel(customer.getName()));
            contentPanel.add(namePanel);
            JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            balancePanel.add(new JLabel("Balance:"));
            balanceValueLabel = new JLabel(String.format("%.2f", customer.getBalance()));
            balancePanel.add(balanceValueLabel);
            contentPanel.add(balancePanel);
            contentPanel.add(Box.createVerticalStrut(20));
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton shopButton = new JButton("Shop");
            JButton inventoryButton = new JButton("View Inventory");
            buttonsPanel.add(shopButton);
            buttonsPanel.add(inventoryButton);
            contentPanel.add(buttonsPanel);
            add(contentPanel, BorderLayout.CENTER);

            shopButton.addActionListener(e -> {
                new CustomerShoppingPage(this, customer); // 'this' is CustomerDetailPage instance
                balanceValueLabel.setText(String.format("%.2f", customer.getBalance()));
                CustomerGUI.this.refreshCustomerList(); // Refresh main list if balance changed in controller
            });
            inventoryButton.addActionListener(e -> {
                new CustomerInventoryPage(this, customer);
                balanceValueLabel.setText(String.format("%.2f", customer.getBalance()));
            });
            setVisible(true);
        }

        public void refreshBalanceDisplay() {
            balanceValueLabel.setText(String.format("%.2f", customer.getBalance()));
        }
    }
    class CustomerShoppingPage extends JDialog {
        private Customers customer;
        private JComboBox<String> productSelectionCombo;
        private JLabel stockAmountLabelValue;
        private JTextField amountToBuyField;
        private List<MarketProductEntry> availableMarketProducts;
        private CustomerDetailPage parentCustomerDetailDialog;

        private class MarketProductEntry {
            Market market; Product product; int stock; double price;
            MarketProductEntry(Market m, Product p, int s, double pr) { market=m; product=p; stock=s; price=pr; }
            @Override public String toString() { // Format: Market_0 | Cable_0
                return String.format("%s | %s", market.getName(), product.getName());
            }
        }

        public CustomerShoppingPage(Dialog owner, Customers customer) {
            super(owner, "Shopping - Customer: " + customer.getName(), true);
            if (owner instanceof CustomerDetailPage) {
                this.parentCustomerDetailDialog = (CustomerDetailPage) owner;
            }
            this.customer = customer;
            this.availableMarketProducts = new ArrayList<>();

            setLayout(new BorderLayout(10,10));
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel topShoppingPanel = new JPanel(new BorderLayout());
            topShoppingPanel.setBorder(new EmptyBorder(5,10,5,10));
            JLabel shoppingTitle = new JLabel("Shopping - Customer: " + customer.getName());
            shoppingTitle.setFont(new Font("Arial", Font.BOLD, 14));
            topShoppingPanel.add(shoppingTitle, BorderLayout.WEST);
            JButton backShoppingButton = new JButton("Back");
            backShoppingButton.addActionListener(e -> dispose());
            topShoppingPanel.add(backShoppingButton, BorderLayout.EAST);
            add(topShoppingPanel, BorderLayout.NORTH);

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

            Dimension labelSize = new Dimension(120, 25);
            Dimension fieldSize = new Dimension(200, 25);

            // Select Product
            JPanel productRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel productLabel = new JLabel("Select Product:");
            productLabel.setPreferredSize(labelSize);
            productRowPanel.add(productLabel);
            productSelectionCombo = new JComboBox<>();
            productSelectionCombo.setPreferredSize(fieldSize);
            productRowPanel.add(productSelectionCombo);
            productRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(productRowPanel);

            //  Stock Amount
            JPanel stockRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel stockLabel = new JLabel("Stock Amount:");
            stockLabel.setPreferredSize(labelSize);
            stockRowPanel.add(stockLabel);
            stockAmountLabelValue = new JLabel("0");
            stockAmountLabelValue.setPreferredSize(fieldSize);
            stockRowPanel.add(stockAmountLabelValue);
            stockRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(stockRowPanel);

            //  Amount to Buy
            JPanel amountRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel amountLabelText = new JLabel("Amount:");
            amountLabelText.setPreferredSize(labelSize);
            amountRowPanel.add(amountLabelText);
            amountToBuyField = new JTextField();
            amountToBuyField.setPreferredSize(fieldSize);
            amountRowPanel.add(amountToBuyField);
            amountRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formPanel.add(amountRowPanel);

            add(formPanel, BorderLayout.CENTER);

            JPanel buyButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            JButton buyButton = new JButton("Buy");
            buyButton.setPreferredSize(new Dimension(100, 30));
            buyButtonPanel.add(buyButton);
            add(buyButtonPanel, BorderLayout.SOUTH);

            populateProductCombo();
            productSelectionCombo.addActionListener(e -> updateStockInfoForSelectedProduct());
            if (productSelectionCombo.getItemCount() > 0) {
                productSelectionCombo.setSelectedIndex(0);
            } else {
                updateStockInfoForSelectedProduct();
            }

            buyButton.addActionListener(e -> {
                int selectedIndex = productSelectionCombo.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= availableMarketProducts.size()) {
                    JOptionPane.showMessageDialog(this, "Please select a product.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MarketProductEntry selectedEntry = availableMarketProducts.get(selectedIndex);
                int amountToBuyNum;
                try {
                    amountToBuyNum = Integer.parseInt(amountToBuyField.getText().trim());
                    if (amountToBuyNum <= 0) {
                        JOptionPane.showMessageDialog(this, "Amount to buy must be a positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    customer.buyProductFromMarket(selectedEntry.product, amountToBuyNum, selectedEntry.market);
                    JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    populateProductCombo();

                    // NOTE TO SELF: DO NOT TOUCH THE FOLLOWING METHOD. IT PREVENTS A LOT OF ERRORS.

                    if (productSelectionCombo.getItemCount() > 0) {
                        int newSelectionIndex = Math.min(selectedIndex, productSelectionCombo.getItemCount() - 1);
                        if (newSelectionIndex >=0) productSelectionCombo.setSelectedIndex(newSelectionIndex);
                        else if (productSelectionCombo.getItemCount() > 0) productSelectionCombo.setSelectedIndex(0);

                    }
                    updateStockInfoForSelectedProduct();
                    amountToBuyField.setText("");

                    if (parentCustomerDetailDialog != null) {
                        parentCustomerDetailDialog.refreshBalanceDisplay();
                    }
                    CustomerGUI.this.refreshCustomerList();

                } catch (IllegalStateException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Purchase Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            pack();
            setMinimumSize(new Dimension(450, 230));
            setLocationRelativeTo(owner);
            setVisible(true);
        }

        private void populateProductCombo() {
            availableMarketProducts.clear();
            Object currentComboSelection = productSelectionCombo.getSelectedItem();
            productSelectionCombo.removeAllItems();

            List<Market> markets = marketController.getMarkets();
            for (Market market : markets) {
                for (Map.Entry<Product, Integer> entry : market.getInventory().entrySet()) {
                    Product product = entry.getKey();
                    int stock = entry.getValue();
                    if (stock > 0) {
                        double price = market.getProductPrice(product);
                        MarketProductEntry mpe = new MarketProductEntry(market, product, stock, price);
                        availableMarketProducts.add(mpe);
                        productSelectionCombo.addItem(mpe.toString());
                    }
                }
            }
            if (currentComboSelection != null) {
                for (int i = 0; i < productSelectionCombo.getItemCount(); i++) {
                    if (productSelectionCombo.getItemAt(i).equals(currentComboSelection.toString())) {
                        productSelectionCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (productSelectionCombo.getSelectedIndex() == -1 && productSelectionCombo.getItemCount() > 0) {
                productSelectionCombo.setSelectedIndex(0);
            }
        }

        private void updateStockInfoForSelectedProduct() {
            int selectedIndex = productSelectionCombo.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < availableMarketProducts.size()) {
                MarketProductEntry selectedEntry = availableMarketProducts.get(selectedIndex);
                stockAmountLabelValue.setText(String.valueOf(selectedEntry.stock));
            } else {
                stockAmountLabelValue.setText("N/A");
            }
        }
    }

    class CustomerInventoryPage extends JDialog {
        private Customers customer;

        public CustomerInventoryPage(Dialog owner, Customers customer) {
            super(owner, "Inventory - " + customer.getName(), true);
            this.customer = customer;

            setLayout(new BorderLayout(10,10));
            setSize(450, 350);
            setLocationRelativeTo(owner);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(new EmptyBorder(5,5,5,5));
            JLabel title = new JLabel("Inventory - Customer: " + customer.getName() +
                    " (Balance: " + String.format("%.2f", customer.getBalance()) + ")");
            topPanel.add(title, BorderLayout.CENTER);
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> dispose());
            topPanel.add(backButton, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            DefaultListModel<String> inventoryListModel = new DefaultListModel<>();
            JList<String> inventoryDisplayList = new JList<>(inventoryListModel);
            JScrollPane scrollPane = new JScrollPane(inventoryDisplayList);
            scrollPane.setBorder(new EmptyBorder(10,10,10,10));

            Map<Product, Integer> inventory = customer.getInventory();
            if (inventory.isEmpty()) {
                inventoryListModel.addElement("No items in inventory.");
            } else {
                for (Map.Entry<Product, Integer> entry : inventory.entrySet()) {
                    Product product = entry.getKey();
                    Integer quantity = entry.getValue();
                    inventoryListModel.addElement(String.format("- %s: %d", product.getName(), quantity));
                }
            }
            add(scrollPane, BorderLayout.CENTER);
            setVisible(true);
        }
    }
}