package view;

import model.Patient;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for editing an existing patient.
 * Only edits core fields used in the table to keep it simple and robust.
 */
public class PatientEditDialog {

    public static Patient showEditDialog(Component parent, Patient existing) {
        if (existing == null) return null;

        JTextField tfName = new JTextField(existing.getName(), 18);
        JTextField tfDob = new JTextField(existing.getDateOfBirth(), 12);
        JTextField tfNhs = new JTextField(existing.getNhsNumber(), 16);
        JTextField tfPhone = new JTextField(existing.getPhone(), 14);
        JTextField tfEmail = new JTextField(existing.getEmail(), 18);
        JTextField tfGp = new JTextField(existing.getGpSurgery(), 12);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Patient ID:")); panel.add(new JLabel(existing.getUserId()));
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("DOB (YYYY-MM-DD):")); panel.add(tfDob);
        panel.add(new JLabel("NHS Number:")); panel.add(tfNhs);
        panel.add(new JLabel("Phone:")); panel.add(tfPhone);
        panel.add(new JLabel("Email:")); panel.add(tfEmail);
        panel.add(new JLabel("GP Surgery ID:")); panel.add(tfGp);

        int ok = JOptionPane.showConfirmDialog(
                parent, panel, "Edit Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (ok != JOptionPane.OK_OPTION) return null;

        // Basic validation
        if (tfName.getText().trim().isEmpty() || tfDob.getText().trim().isEmpty() || tfNhs.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Name/DOB/NHS Number cannot be empty.");
            return null;
        }

        return new Patient(
                existing.getUserId(),
                tfName.getText().trim(),
                tfEmail.getText().trim(),
                tfPhone.getText().trim(),
                "", // password not used
                tfNhs.getText().trim(),
                tfDob.getText().trim(),
                tfGp.getText().trim()
        );
    }
}
