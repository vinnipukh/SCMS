import javax.swing.*;

public class MyWindow extends JFrame {
    /**
     * This is the superclass for all the NORMAL frames in my program
     * @param windowName
     */
    public MyWindow(String windowName){
        super(windowName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,400);
        setVisible(true);

    }

    

}
