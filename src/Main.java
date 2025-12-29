import controller.*;
import view.MainFrame;

import javax.swing.*;
import java.util.Locale;

/**
 * Application entry point.
 * Forces Swing UI language to English for consistent marking.
 */
public class Main {
    public static void main(String[] args) {

        // Force English labels for Swing option dialogs (OK/Cancel/Yes/No)
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

                AppointmentController ac = new AppointmentController();
                ac.load("data/appointments.csv");

                ReferralController rc = new ReferralController();
                rc.load("data/referrals.csv");

                PrescriptionController prc = new PrescriptionController();
                prc.load("data/prescriptions.csv");

                MainFrame frame = new MainFrame(pc, cc, ac, rc, prc);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
