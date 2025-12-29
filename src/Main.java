import controller.PatientController;
import view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                PatientController pc = new PatientController();
                pc.load("data/patients.csv");

                MainFrame frame = new MainFrame(pc);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

