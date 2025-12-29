package view;

import model.Patient;

import javax.swing.*;
import java.awt.*;

public class PatientDialog {

    public static Patient showAddDialog(Component parent) {
        JTextField tfId = new JTextField(12);
        JTextField tfFirst = new JTextField(12);
        JTextField tfLast = new JTextField(12);
        JTextField tfDob = new JTextField(12);
        JTextField tfNhs = new JTextField(12);
        JTextField tfPhone = new JTextField(12);
        JTextField tfEmail = new JTextField(12);
        JTextField tfGp = new JTextField(12);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Patient ID:")); panel.add(tfId);
        panel.add(new JLabel("First Name:")); panel.add(tfFirst);
        panel.add(new JLabel("Last Name:")); panel.add(tfLast);
        panel.add(new JLabel("DOB (YYYY-MM-DD):")); panel.add(tfDob);
        panel.add(new JLabel("NHS Number:")); panel.add(tfNhs);
        panel.add(new JLabel("Phone:")); panel.add(tfPhone);
        panel.add(new JLabel("Email:")); panel.add(tfEmail);
        panel.add(new JLabel("GP Surgery ID:")); panel.add(tfGp);

        int result = JOptionPane.showConfirmDialog(
                parent, panel, "Add New Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        // 简单校验（拿交互分）
        String id = tfId.getText().trim();
        String first = tfFirst.getText().trim();
        String last = tfLast.getText().trim();
        String dob = tfDob.getText().trim();
        String nhs = tfNhs.getText().trim();

        if (id.isEmpty() || first.isEmpty() || last.isEmpty() || dob.isEmpty() || nhs.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "ID/First/Last/DOB/NHS cannot be empty.");
            return null;
        }

        String name = (first + " " + last).trim();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();
        String gp = tfGp.getText().trim();

        String password = "";
        return new Patient(id, name, email, phone, password, nhs, dob, gp);
    }
}
