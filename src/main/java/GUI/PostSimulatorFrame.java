package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PostSimulatorFrame extends JFrame {
    private PostSimulatorPanel postSimulatorPanel;

    private void initMainPanel() {
        postSimulatorPanel = new PostSimulatorPanel();

    }

    private void initListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                onQuit();
            }
        });
    }

    private void onQuit() {
        String[] vars = {"Yes", "No"};
        int result = JOptionPane.showOptionDialog(this, "Are you sure want to quit?",
                "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, vars, "Yes");
        if (result == JOptionPane.YES_OPTION)
            System.exit(0);
    }

    public PostSimulatorFrame(String s) {
        super(s);
        setSize(1200, 600);
        this.setLayout(new BorderLayout());
        initMainPanel();
        setVisible(true);
        initListeners();
        this.add(postSimulatorPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMaximumSize(getSize());
        setMinimumSize(getSize());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PostSimulatorFrame("Post machine simulator"));
    }


}
