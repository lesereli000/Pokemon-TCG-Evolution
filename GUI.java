import javax.swing.JFrame;

public class GUI {

    private void createGUI() {
        final int frameWidth = 1000;
        final int frameHeight = 800;
        final int frameXLoc = 100;
        final int frameYLoc = 100;

        final int cardWidth = 50;
        final int cardHeight = 40;

        // Creating the JFrame
        JFrame frame = new JFrame();
        frame.setTitle("Pokemon Game");
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(frameXLoc, frameYLoc);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createGUI();
    }

}


