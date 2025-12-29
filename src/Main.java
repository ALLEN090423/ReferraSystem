import controller.ClinicianController;
import controller.PatientController;
import controller.ReferralController;
import view.MainFrame;

import javax.swing.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        Locale.setDefault(Locale.ENGLISH);
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");

        SwingUtilities.invokeLater(() -> {
            try {
                PatientController pc = new PatientController();
                pc.load("data/patients.csv");

                ClinicianController cc = new ClinicianController();
                cc.load("data/clinicians.csv");

                ReferralController rc = new ReferralController();
                rc.load("data/referrals.csv");

                MainFrame frame = new MainFrame(pc, cc, rc);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
