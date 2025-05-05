import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage extends MyWindow{
    public MainPage(){
        super("Supply Chain Management System");

        JPanel mainpanel = new JPanel();
        setLayout(new BorderLayout(10,10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBorder(new EmptyBorder(5,10,10,5));
        JButton exitButton = new JButton("Exit");

        exitButton.addActionListener(e -> {
            System.exit(0);
            // i wrote zero here cause thats the  exit status code
        });

        mainpanel.setLayout(new GridLayout(4,1,10,10));
        JButton rmcsButton = new JButton("Raw Material Producers");
        JButton factoriesButton = new JButton("Factories");
        JButton marketsButton = new JButton("Markets");
        JButton customersButton = new JButton("Customers");
        Dimension buttonSizeButton = new Dimension(250, 60);

        rmcsButton.setPreferredSize(buttonSizeButton);
        factoriesButton.setPreferredSize(buttonSizeButton);
        marketsButton.setPreferredSize(buttonSizeButton);
        customersButton.setPreferredSize(buttonSizeButton);


        mainpanel.add(rmcsButton);
        mainpanel.add(factoriesButton);
        mainpanel.add(marketsButton);
        mainpanel.add(customersButton);
        add(mainpanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        validate();







    }
}
