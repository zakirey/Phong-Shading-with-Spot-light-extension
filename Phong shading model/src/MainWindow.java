import javax.swing.*;

public class MainWindow extends JFrame {

    PhongPanel phong;
    public MainWindow() {
        super("Renderer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        Cone light modification is applicable to color.txt for now, if you want to run it on other scenes please add
//                values needed for Cone light to other scene descriptions
        phong = new PhongPanel("glossyPlastic", this, false);
        add(phong);
        pack();
        setVisible(true);
    }

}
