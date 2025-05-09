import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FactoryGUI extends MyWindow {
    DefaultListModel<String> factoryListModel;
    JList<String> factoryList;
    FactoryController controller;

    public FactoryGUI(FactoryController controller) {
        super("Factories");
        setLayout(new BorderLayout());
        this.controller = controller;

        // Top panel with title and Back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Factory List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // List of factories
        factoryListModel = new DefaultListModel<>();
        factoryList = new JList<>(factoryListModel);
        JScrollPane scrollPane = new JScrollPane(factoryList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Add/Edit buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add New Factory");
        JButton editButton = new JButton("Edit Factory");
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshFactoryList();

        // Double-click to open detail page
        factoryList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = factoryList.locationToIndex(evt.getPoint());
                    if (index >= 0 && index < controller.getFactories().size()) {
                        new FactoryDetailPage(controller.getFactory(index));
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            new AddFactoryDialog(FactoryGUI.this);
            refreshFactoryList();
        });

        editButton.addActionListener(e -> {
            int selectedIndex = factoryList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a factory to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new EditFactoryDialog(FactoryGUI.this, controller.getFactory(selectedIndex));
            refreshFactoryList();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void refreshFactoryList() {
        factoryListModel.clear();
        List<Factory> factories = controller.getFactories();
        java.util.Map<String, Integer> nameCount = new java.util.HashMap<>();
        for (Factory f : factories) {
            nameCount.put(f.getName(), nameCount.getOrDefault(f.getName(), 0) + 1);
        }
        for (Factory f : factories) {
            String display = f.getName();
            if (nameCount.get(f.getName()) > 1) {
                display += " (" + f.getFactoryID() + ")";
            }
            factoryListModel.addElement(display);
        }
    }
}

// Dialog for factory details (individual factory page)
class FactoryDetailPage extends JDialog {
    public FactoryDetailPage(Factory factory) {
        super((JFrame) null, "Factory: " + factory.getName(), true);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Factory: " + factory.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Main panel with BoxLayout (vertical)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Helper for row panels
        int rowHeight = 32;
        int labelWidth = 130;
        int fieldWidth = 200;

        // Name
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        JLabel nameValue = new JLabel(factory.getName());
        nameValue.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        namePanel.add(nameLabel);
        namePanel.add(nameValue);
        mainPanel.add(namePanel);

        // Balance
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel balanceLabel = new JLabel("Balance:");
        balanceLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        JLabel balanceValueLabel = new JLabel(String.valueOf(factory.getBalance()));
        balanceValueLabel.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceValueLabel);
        mainPanel.add(balancePanel);

        // Capacity
        JPanel capacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        JLabel capacityValue = new JLabel(String.valueOf(factory.getCapacity()));
        capacityValue.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        capacityPanel.add(capacityLabel);
        capacityPanel.add(capacityValue);
        mainPanel.add(capacityPanel);

        // Select Raw Material
        JPanel rawMaterialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel rawMaterialLabel = new JLabel("Select Raw Material:");
        rawMaterialLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        JComboBox<String> rawMaterialCombo = new JComboBox<>();
        rawMaterialCombo.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        java.util.List<RawMaterialProducer> producers = RawMaterialProducerGUI.getAllProducers();
        java.util.Map<String, RawMaterialProducer> materialProducerMap = new java.util.HashMap<>();
        for (RawMaterialProducer p : producers) {
            String entry = p.getMaterialProduced().getName() + " (" + p.getName() + ")";
            rawMaterialCombo.addItem(entry);
            materialProducerMap.put(entry, p);
        }
        rawMaterialPanel.add(rawMaterialLabel);
        rawMaterialPanel.add(rawMaterialCombo);
        mainPanel.add(rawMaterialPanel);

        // Amount
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setPreferredSize(new Dimension(labelWidth, rowHeight));
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(fieldWidth, rowHeight));
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);

        // Buy button (full width, centered)
        JButton buyButton = new JButton("Buy");
        buyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
        buyButton.setPreferredSize(new Dimension(fieldWidth + labelWidth, rowHeight));
        JPanel buyButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buyButtonPanel.add(buyButton);
        mainPanel.add(buyButtonPanel);

        // Action buttons (full width, centered, stacked)
        Dimension buttonSize = new Dimension(fieldWidth + labelWidth, rowHeight);
        JButton createDesignButton = new JButton("Create Design");
        createDesignButton.setMaximumSize(buttonSize);
        createDesignButton.setPreferredSize(buttonSize);
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createPanel.add(createDesignButton);
        mainPanel.add(createPanel);
        JButton produceButton = new JButton("Produce Product");
        produceButton.setMaximumSize(buttonSize);
        produceButton.setPreferredSize(buttonSize);
        JPanel producePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        producePanel.add(produceButton);
        mainPanel.add(producePanel);
        JButton destroyButton = new JButton("Destroy Byproducts");
        destroyButton.setMaximumSize(buttonSize);
        destroyButton.setPreferredSize(buttonSize);
        JPanel destroyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        destroyPanel.add(destroyButton);
        mainPanel.add(destroyPanel);
        JButton inventoryButton = new JButton("View Inventory");
        inventoryButton.setMaximumSize(buttonSize);
        inventoryButton.setPreferredSize(buttonSize);
        JPanel inventoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inventoryPanel.add(inventoryButton);
        mainPanel.add(inventoryPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Buy logic (find producer by selected combo entry)
        buyButton.addActionListener(e -> {
            try {
                String selectedEntry = (String) rawMaterialCombo.getSelectedItem();
                if (selectedEntry == null) {
                    JOptionPane.showMessageDialog(this, "Please select a raw material.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int amount;
                try {
                    amount = Integer.parseInt(amountText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                RawMaterialProducer selectedProducer = materialProducerMap.get(selectedEntry);
                if (selectedProducer == null) {
                    JOptionPane.showMessageDialog(this, "No producer found for selected raw material.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String material = selectedProducer.getMaterialProduced().getName();
                double price = selectedProducer.getSellingPrice();
                if (selectedProducer.getStock() < amount) {
                    JOptionPane.showMessageDialog(this, "Producer does not have enough stock.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    factory.buyProduct(material, amount, price);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    selectedProducer.sell(amount);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                balanceValueLabel.setText(String.valueOf(factory.getBalance()));
                JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action button listeners
        createDesignButton.addActionListener(e -> new CreateDesignDialog(factory, null, () -> {}, () -> {}));
        produceButton.addActionListener(e -> new ProduceDialog(factory, balanceValueLabel));
        destroyButton.addActionListener(e -> new DestroyDialog(factory, balanceValueLabel));
        inventoryButton.addActionListener(e -> new InventoryDialog(factory));

        setSize(420, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Dialog for adding a new factory
class AddFactoryDialog extends JDialog {
    public AddFactoryDialog(FactoryGUI parent) {
        super((JFrame) null, "Add New Factory", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel nameLabel = new JLabel("Factory Name:");
        JTextField nameField = new JTextField();
        JLabel balanceLabel = new JLabel("Initial Balance:");
        JTextField balanceField = new JTextField();
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField();
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(balanceLabel); formPanel.add(balanceField);
        formPanel.add(capacityLabel); formPanel.add(capacityField);
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
                double balance = Double.parseDouble(balanceField.getText().trim());
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (balance < 0) throw new IllegalArgumentException("Initial balance cannot be negative.");
                if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive.");
                parent.controller.addFactory(new Factory(name, balance, capacity));
                parent.refreshFactoryList();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for balance and capacity.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Dialog for editing a factory
class EditFactoryDialog extends JDialog {
    public EditFactoryDialog(FactoryGUI parent, Factory factory) {
        super((JFrame) null, "Edit Factory", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel nameLabel = new JLabel("Factory Name:");
        JTextField nameField = new JTextField(factory.getName());
        JLabel balanceLabel = new JLabel("Balance:");
        JTextField balanceField = new JTextField(String.valueOf(factory.getBalance()));
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(String.valueOf(factory.getCapacity()));
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(balanceLabel); formPanel.add(balanceField);
        formPanel.add(capacityLabel); formPanel.add(capacityField);
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
                double balance = Double.parseDouble(balanceField.getText().trim());
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative.");
                if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive.");
                // Only update balance and capacity (name is final in Factory model)
                factory.setBalance(balance);
                factory.setCapacity(capacity);
                parent.refreshFactoryList();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for balance and capacity.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Dialog for creating a new product design
class CreateDesignDialog extends JDialog {
    public CreateDesignDialog(Factory factory, JComboBox<String> designCombo, Runnable updateInventory, Runnable updateByproductCombo) {
        super((JFrame) null, "Create Design", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        formPanel.setBorder(new EmptyBorder(15, 40, 15, 40));
        JTextField productNameField = new JTextField();
        JTextField productCostField = new JTextField();
        JTextField byproductNameField = new JTextField();
        JTextField byproductAmountField = new JTextField();
        JTextField byproductCostField = new JTextField();
        // Raw material selection
        java.util.Set<String> rawMaterialNames = new java.util.HashSet<>();
        for (RawMaterialProducer p : RawMaterialProducerGUI.getAllProducers()) {
            rawMaterialNames.add(p.getMaterialProduced().getName());
        }
        rawMaterialNames.addAll(factory.getRawMaterials().keySet());
        JComboBox<String> inputMaterialCombo = new JComboBox<>(rawMaterialNames.toArray(new String[0]));
        JLabel selectedStockLabel = new JLabel();
        if (inputMaterialCombo.getItemCount() > 0) {
            String selected = (String) inputMaterialCombo.getSelectedItem();
            selectedStockLabel.setText(String.valueOf(factory.getRawMaterials().getOrDefault(selected, 0)));
        }
        inputMaterialCombo.addActionListener(e -> {
            String selected = (String) inputMaterialCombo.getSelectedItem();
            selectedStockLabel.setText(String.valueOf(factory.getRawMaterials().getOrDefault(selected, 0)));
        });
        JTextField inputAmountField = new JTextField();
        JButton addInputButton = new JButton("Add Input");
        DefaultListModel<String> inputListModel = new DefaultListModel<>();
        JList<String> inputList = new JList<>(inputListModel);
        java.util.Map<String, Double> inputRequirements = new java.util.HashMap<>();
        addInputButton.addActionListener(e -> {
            String material = (String) inputMaterialCombo.getSelectedItem();
            String amtText = inputAmountField.getText().trim();
            if (material == null || amtText.isEmpty()) return;
            try {
                double amt = Double.parseDouble(amtText);
                if (amt <= 0) throw new NumberFormatException();
                inputRequirements.put(material, amt);
                inputListModel.addElement(material + ": " + amt);
                inputAmountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for input amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(productNameField);
        formPanel.add(new JLabel("Product Cost:"));
        formPanel.add(productCostField);
        formPanel.add(new JLabel("Byproduct Name:"));
        formPanel.add(byproductNameField);
        formPanel.add(new JLabel("Byproduct Amount:"));
        formPanel.add(byproductAmountField);
        formPanel.add(new JLabel("Byproduct Cost:"));
        formPanel.add(byproductCostField);
        formPanel.add(new JLabel("Input (RawMaterial):"));
        formPanel.add(inputMaterialCombo);
        formPanel.add(new JLabel("Selected Stock:"));
        formPanel.add(selectedStockLabel);
        formPanel.add(new JLabel("Input Amount:"));
        formPanel.add(inputAmountField);
        JPanel inputButtonPanel = new JPanel(new BorderLayout(8, 8));
        inputButtonPanel.add(addInputButton, BorderLayout.WEST);
        inputButtonPanel.add(new JScrollPane(inputList), BorderLayout.CENTER);
        formPanel.add(inputButtonPanel);
        add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save Design");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        saveButton.addActionListener(e -> {
            try {
                String productName = productNameField.getText().trim();
                String productCostText = productCostField.getText().trim();
                String byproductName = byproductNameField.getText().trim();
                String byproductAmountText = byproductAmountField.getText().trim();
                String byproductCostText = byproductCostField.getText().trim();
                if (productName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Product name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (byproductName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Byproduct name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double productCost, byproductAmount, byproductCost;
                try {
                    productCost = Double.parseDouble(productCostText);
                    byproductAmount = Double.parseDouble(byproductAmountText);
                    byproductCost = Double.parseDouble(byproductCostText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product cost, byproduct amount, and byproduct cost must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (productCost <= 0) {
                    JOptionPane.showMessageDialog(this, "Product cost must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (byproductAmount < 0) {
                    JOptionPane.showMessageDialog(this, "Byproduct amount cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (byproductCost < 0) {
                    JOptionPane.showMessageDialog(this, "Byproduct cost cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validate all input requirements are positive
                for (String mat : inputRequirements.keySet()) {
                    double amt = inputRequirements.get(mat);
                    if (amt <= 0) {
                        JOptionPane.showMessageDialog(this, "Input amount for '" + mat + "' must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Product product = new Product(productName, productCost);
                ByProduct byproduct = new ByProduct(byproductName, byproductCost);
                // Use the first input as the main raw material for ProductDesign constructor
                RawMaterial mainRaw = new RawMaterial(inputRequirements.keySet().iterator().next());
                ProductDesign design = new ProductDesign(product, byproduct, mainRaw);
                design.setByproductAmount(byproductAmount);
                design.setProductionCost(productCost);
                for (String mat : inputRequirements.keySet()) {
                    RawMaterial rm = new RawMaterial(mat);
                    design.addInputRequirement(rm, inputRequirements.get(mat));
                }
                factory.addDesign(design);
                if (designCombo != null) designCombo.addItem(productName);
                updateInventory.run();
                updateByproductCombo.run();
                JOptionPane.showMessageDialog(this, "Design saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// --- ProduceDialog ---
class ProduceDialog extends JDialog {
    public ProduceDialog(Factory factory, JLabel balanceValueLabel) {
        super((JFrame) null, "Produce Product", true);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Select Design:"));
        JComboBox<String> designCombo = new JComboBox<>();
        for (ProductDesign d : factory.getDesigns()) {
            designCombo.addItem(d.getProduct().getName());
        }
        panel.add(designCombo);
        panel.add(new JLabel("Amount to Produce:"));
        JTextField amountField = new JTextField();
        panel.add(amountField);
        add(panel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton produceButton = new JButton("Produce");
        JButton cancelButton = new JButton("Back");
        buttonPanel.add(produceButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        produceButton.addActionListener(e -> {
            try {
                int selectedIndex = designCombo.getSelectedIndex();
                if (selectedIndex == -1) throw new IllegalArgumentException("Please select a design.");
                int amount = Integer.parseInt(amountField.getText().trim());
                ProductDesign design = factory.getDesigns().get(selectedIndex);
                factory.produceProduct(design, amount);
                balanceValueLabel.setText(String.valueOf(factory.getBalance()));
                JOptionPane.showMessageDialog(this, "Production successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 180);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// --- DestroyDialog ---
class DestroyDialog extends JDialog {
    public DestroyDialog(Factory factory, JLabel balanceValueLabel) {
        super((JFrame) null, "Destroy Byproducts", true);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Select Byproduct:"));
        JComboBox<String> byproductCombo = new JComboBox<>();
        for (String byp : factory.getByproducts().keySet()) {
            byproductCombo.addItem(byp);
        }
        panel.add(byproductCombo);
        panel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField();
        panel.add(amountField);
        add(panel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton destroyButton = new JButton("Destroy");
        JButton cancelButton = new JButton("Back");
        buttonPanel.add(destroyButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        destroyButton.addActionListener(e -> {
            try {
                String byproduct = (String) byproductCombo.getSelectedItem();
                int amount = Integer.parseInt(amountField.getText().trim());
                double cost = 5; // Use a fixed cost per unit
                factory.destroyByproduct(byproduct, amount, cost);
                balanceValueLabel.setText(String.valueOf(factory.getBalance()));
                JOptionPane.showMessageDialog(this, "Byproduct destroyed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for amount.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// --- InventoryDialog ---
class InventoryDialog extends JDialog {
    public InventoryDialog(Factory factory) {
        super((JFrame) null, "Inventory - " + factory.getName(), true);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextArea productsArea = new JTextArea(5, 30);
        productsArea.setEditable(false);
        JTextArea byproductsArea = new JTextArea(5, 30);
        byproductsArea.setEditable(false);
        panel.add(new JLabel("Products:"));
        panel.add(new JScrollPane(productsArea));
        panel.add(new JLabel("Byproducts:"));
        panel.add(new JScrollPane(byproductsArea));
        JButton closeButton = new JButton("Back");
        panel.add(closeButton);
        add(panel, BorderLayout.CENTER);
        StringBuilder prod = new StringBuilder();
        for (String k : factory.getProducts().keySet()) {
            prod.append(k).append(": ").append(factory.getProducts().get(k)).append("\n");
        }
        productsArea.setText(prod.toString());
        StringBuilder byp = new StringBuilder();
        for (String k : factory.getByproducts().keySet()) {
            byp.append(k).append(": ").append(factory.getByproducts().get(k)).append("\n");
        }
        byproductsArea.setText(byp.toString());
        closeButton.addActionListener(e -> dispose());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
} 