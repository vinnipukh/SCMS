// ================================================
// FILE: src/MainPage.java (Updated)
// ================================================
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// ActionListener is used via lambdas, so direct import isn't strictly necessary unless you have separate classes.

/**
 * MainPage class implements the Main Page which is shown in the first photo in the project description.
 */
public class MainPage extends MyWindow {
    FactoryController factoryController;
    MarketController marketController;     // Added
    CustomerController customerController; // Added

    public MainPage() {
        super("Supply Chain Management System"); // This will set EXIT_ON_CLOSE by MyWindow's constructor

        // Instantiate controllers
        factoryController = new FactoryController();
        marketController = new MarketController();
        customerController = new CustomerController();

        // Override default close operation for the MainPage itself if it's not desired to exit
        // However, MainPage is usually the main window, so EXIT_ON_CLOSE is often correct for it.
        // We will handle sub-windows' close operations individually.

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Supply Chain Management System", SwingConstants.LEFT);
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(70, 28));
        exitButton.addActionListener(e -> {
            System.exit(0); // Clean exit
        });
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);


        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridLayout(4, 1, 20, 20));


        JButton rmcsButton = new JButton("Raw Material Producers");
        JButton factoriesButton = new JButton("Factories");
        JButton marketsButton = new JButton("Markets");
        JButton customersButton = new JButton("Customers");

        rmcsButton.addActionListener(e -> {
            RawMaterialProducerGUI rmpg = new RawMaterialProducerGUI();
            // RawMaterialProducerGUI extends MyWindow, so its default is EXIT_ON_CLOSE.
            // We want it to DISPOSE_ON_CLOSE so it doesn't close the whole app.
            rmpg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        factoriesButton.addActionListener(e -> {
            FactoryGUI fg = new FactoryGUI(factoryController);
            // FactoryGUI extends MyWindow. Override its close operation.
            fg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        marketsButton.addActionListener(e -> {
            // MarketGUI will be refactored to take MarketController.
            // For now, assuming the old constructor. We'll update this call when MarketGUI is refactored.
            // MarketGUI mg = new MarketGUI(factoryController.getFactories());
            // WHEN MarketGUI is refactored, it will be:
            MarketGUI mg = new MarketGUI(factoryController.getFactories(), marketController);
            mg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // MarketGUI extends MyWindow
        });

        customersButton.addActionListener(e -> {
            // CustomerGUI will be a new JFrame. It should handle its own DISPOSE_ON_CLOSE.
            // We will create CustomerGUI.java later.
            CustomerGUI cg = new CustomerGUI(customerController, marketController);
            // If CustomerGUI extends MyWindow, we'd add:
            // cg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // But since we'll make it extend JFrame directly, it will set its own.
        });

        mainPanel.add(rmcsButton);
        mainPanel.add(factoriesButton);
        mainPanel.add(marketsButton);
        mainPanel.add(customersButton);

        add(mainPanel, BorderLayout.CENTER);
        pack(); // Adjusts window size to fit components
        setLocationRelativeTo(null); // Center on screen
        // MyWindow's constructor calls setVisible(true), so no need here.
        validate(); // Ensures layout is correctly applied
    }

    // Main method to run the application (if not already in your Main.java)
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new MainPage();
    //     });
    // }
}