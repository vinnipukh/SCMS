import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainPage class implements the Main Page which is shown in the first photo in the project description.
 */
public class MainPage extends MyWindow {
    FactoryController factoryController;
    MarketController marketController;
    CustomerController customerController;

    public MainPage() {
        super("Supply Chain Management System");

        factoryController = new FactoryController();
        marketController = new MarketController();
        customerController = new CustomerController();


        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Supply Chain Management System", SwingConstants.LEFT);
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(70, 28));
        exitButton.addActionListener(e -> {
            System.exit(0); // i used 0 here cause thats the exit code
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
            rmpg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        factoriesButton.addActionListener(e -> {
            FactoryGUI fg = new FactoryGUI(factoryController);
            fg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        marketsButton.addActionListener(e -> {
            MarketGUI mg = new MarketGUI(factoryController.getFactories(), marketController);
            mg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // MarketGUI extends MyWindow
        });

        customersButton.addActionListener(e -> {
            CustomerGUI cg = new CustomerGUI(customerController, marketController);
        });

        mainPanel.add(rmcsButton);
        mainPanel.add(factoriesButton);
        mainPanel.add(marketsButton);
        mainPanel.add(customersButton);

        add(mainPanel, BorderLayout.CENTER);
        pack(); // adjusts window size
        setLocationRelativeTo(null); // makes it appear on the center of the screen
        validate();
        setVisible(true);
    }

}