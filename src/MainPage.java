import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainPage class implements the Main Page which is shown in the first photo in the project description.
 * 
 *
 */
public class MainPage extends MyWindow{
    
    public MainPage(){
        super("Supply Chain Management System");

        
        setLayout(new BorderLayout(10,10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Supply Chain Management System", SwingConstants.LEFT);        
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(70, 28));
        exitButton.addActionListener(e -> {
            System.exit(0);
            // i wrote zero here cause thats the  exit status code
        });
        topPanel.add(exitButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new GridLayout(4,1,20,20));       

        
        JButton rmcsButton = new JButton("Raw Material Producers");
        JButton factoriesButton = new JButton("Factories");
        JButton marketsButton = new JButton("Markets");
        JButton customersButton = new JButton("Customers");

        rmcsButton.addActionListener(e -> {
            new RawMaterialProducerGUI();
        });

        mainPanel.add(rmcsButton);
        mainPanel.add(factoriesButton);
        mainPanel.add(marketsButton);
        mainPanel.add(customersButton);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        validate();
    }

   
}
