import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RawMaterialProducerGUI extends MyWindow {
    DefaultListModel<String> producerListModel;
    JList<String> producerList;
    java.util.List<RawMaterialProducer> producers;

    public RawMaterialProducerGUI() {
        super("Raw Material Producers");
        setLayout(new BorderLayout());

        // Top panel with title and Back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Raw Material Producers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // List of producers
        producerListModel = new DefaultListModel<>();
        producerList = new JList<>(producerListModel);
        JScrollPane scrollPane = new JScrollPane(producerList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Add/Edit buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add New Producer");
        JButton editButton = new JButton("Edit Producer");
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Dummy data for demonstration
        producers = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            producers.add(new RawMaterialProducer(
                "Copper_" + i,
                5000.0,
                1200.0,
                new RawMaterial("Copper"),
                10.0
            ));
            producers.get(i).setBalance(5000.0);
            producers.get(i).setStorageCapacity(1200.0);
            producers.get(i).setSellingPrice(10.0);
            producers.get(i).setProducerName("Copper_" + i);
        }
        refreshProducerList();

        // Double-click to open detail page
        producerList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = producerList.locationToIndex(evt.getPoint());
                    if (index >= 0 && index < producers.size()) {
                        new ProducerDetailPage(producers.get(index));
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            new AddProducerDialog(RawMaterialProducerGUI.this);
            refreshProducerList();
        });

        editButton.addActionListener(e -> {
            int selectedIndex = producerList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a producer to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new EditProducerDialog(RawMaterialProducerGUI.this, producers.get(selectedIndex));
            refreshProducerList();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper method to update the producer list display
    void refreshProducerList() {
        producerListModel.clear();
        java.util.Map<String, Integer> nameCount = new java.util.HashMap<>();
        for (RawMaterialProducer p : producers) {
            nameCount.put(p.getName(), nameCount.getOrDefault(p.getName(), 0) + 1);
        }
        for (RawMaterialProducer p : producers) {
            String display = p.getName();
            if (nameCount.get(p.getName()) > 1) {
                display += " (" + p.getProducerID() + ")";
            }
            producerListModel.addElement(display);
        }
    }
}

// Inner class for the individual producer detail page
class ProducerDetailPage extends JDialog {
    public ProducerDetailPage(RawMaterialProducer producer) {
        super((JFrame) null, "Producer: " + producer.getName(), true);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel("Producer: " + producer.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(producer.getName()));
        infoPanel.add(new JLabel("Fund:"));
        JLabel fundValueLabel = new JLabel(String.valueOf(producer.getBalance()));
        infoPanel.add(fundValueLabel);
        infoPanel.add(new JLabel("Stock:"));
        JLabel stockValueLabel = new JLabel(String.valueOf(producer.getStock()));
        infoPanel.add(stockValueLabel);
        infoPanel.add(new JLabel("Capacity:"));
        infoPanel.add(new JLabel(String.valueOf(producer.getStorageCapacity())));
        add(infoPanel, BorderLayout.CENTER);

        JPanel producePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        producePanel.add(new JLabel("Amount to Produce:"));
        JTextField amountField = new JTextField(10);
        producePanel.add(amountField);
        JButton produceButton = new JButton("Produce");
        producePanel.add(produceButton);
        add(producePanel, BorderLayout.SOUTH);

        produceButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                producer.produce(amount);
                fundValueLabel.setText(String.valueOf(producer.getBalance()));
                stockValueLabel.setText(String.valueOf(producer.getStock()));
                JOptionPane.showMessageDialog(this, "Produced successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Inner class for the Add New Producer dialog
class AddProducerDialog extends JDialog {
    public AddProducerDialog(RawMaterialProducerGUI parent) {
        super((JFrame) null, "Add New Producer", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel costLabel = new JLabel("Cost:");
        JTextField costField = new JTextField();
        JLabel priceLabel = new JLabel("Selling Price:");
        JTextField priceField = new JTextField();
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField();
        JLabel fundLabel = new JLabel("Initial Fund:");
        JTextField fundField = new JTextField();
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(costLabel); formPanel.add(costField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(capacityLabel); formPanel.add(capacityField);
        formPanel.add(fundLabel); formPanel.add(fundField);
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
                double cost = Double.parseDouble(costField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                double capacity = Double.parseDouble(capacityField.getText().trim());
                double fund = Double.parseDouble(fundField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                RawMaterial mat = new RawMaterial(name);
                mat.setProductionCost(cost);
                RawMaterialProducer newProducer = new RawMaterialProducer(name, fund, capacity, mat, price);
                parent.producers.add(newProducer);
                parent.refreshProducerList();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for cost, price, capacity, and fund.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

// Inner class for the Edit Producer dialog
class EditProducerDialog extends JDialog {
    public EditProducerDialog(RawMaterialProducerGUI parent, RawMaterialProducer producer) {
        super((JFrame) null, "Edit Producer", true);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(producer.getName());
        JLabel costLabel = new JLabel("Cost:");
        JTextField costField = new JTextField(String.valueOf(producer.getMaterialProduced().getProductionCost()));
        JLabel priceLabel = new JLabel("Selling Price:");
        JTextField priceField = new JTextField(String.valueOf(producer.getSellingPrice()));
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(String.valueOf(producer.getStorageCapacity()));
        JLabel fundLabel = new JLabel("Initial Fund:");
        JTextField fundField = new JTextField(String.valueOf(producer.getBalance()));
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(costLabel); formPanel.add(costField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(capacityLabel); formPanel.add(capacityField);
        formPanel.add(fundLabel); formPanel.add(fundField);
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
                double cost = Double.parseDouble(costField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                double capacity = Double.parseDouble(capacityField.getText().trim());
                double fund = Double.parseDouble(fundField.getText().trim());
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (fund < 0) throw new IllegalArgumentException("Initial fund cannot be negative.");
                producer.setProducerName(name);
                producer.getMaterialProduced().setProductionCost(cost);
                producer.setSellingPrice(price);
                producer.setStorageCapacity(capacity);
                producer.setBalance(fund);
                parent.refreshProducerList();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for cost, price, capacity, and fund.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());
        setSize(350, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
