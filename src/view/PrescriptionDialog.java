package view;

import controller.PrescriptionController;
import model.Clinician;
import model.Patient;
import model.Prescription;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PrescriptionDialog {

    public static Prescription showAddDialog(Component parent,
                                             List<Patient> patients,
                                             List<Clinician> clinicians,
                                             PrescriptionController prescriptionController) throws Exception {

        JComboBox<String> cbPatient = new JComboBox<>();
        for (Patient p : patients) cbPatient.addItem(p.getUserId() + " - " + p.getName());

        JComboBox<String> cbClinician = new JComboBox<>();
        for (Clinician c : clinicians) cbClinician.addItem(c.getUserId() + " - " + c.getName() + " (" + c.getRole() + ")");

        JTextField tfAppointmentId = new JTextField(12);
        JTextField tfMedication = new JTextField(18);
        JTextField tfDosage = new JTextField(12);
        JTextField tfFrequency = new JTextField(12);
        JTextField tfDuration = new JTextField(8);
        JTextField tfQuantity = new JTextField(12);
        JTextArea taInstructions = new JTextArea(5, 22);
        JTextField tfPharmacy = new JTextField(18);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Issued", "Collected"});
        JTextField tfCollectionDate = new JTextField(12); // 可空

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Patient:")); panel.add(cbPatient);
        panel.add(new JLabel("Clinician:")); panel.add(cbClinician);
        panel.add(new JLabel("Appointment ID (optional):")); panel.add(tfAppointmentId);
        panel.add(new JLabel("Medication Name:")); panel.add(tfMedication);
        panel.add(new JLabel("Dosage:")); panel.add(tfDosage);
        panel.add(new JLabel("Frequency:")); panel.add(tfFrequency);
        panel.add(new JLabel("Duration Days:")); panel.add(tfDuration);
        panel.add(new JLabel("Quantity:")); panel.add(tfQuantity);
        panel.add(new JLabel("Instructions:")); panel.add(new JScrollPane(taInstructions));
        panel.add(new JLabel("Pharmacy Name:")); panel.add(tfPharmacy);
        panel.add(new JLabel("Status:")); panel.add(cbStatus);
        panel.add(new JLabel("Collection Date (optional):")); panel.add(tfCollectionDate);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setPreferredSize(new Dimension(520, 520));

        int ok = JOptionPane.showConfirmDialog(parent, scroll, "Add Prescription",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return null;

        // 校验：这些最关键
        if (tfMedication.getText().trim().isEmpty() || tfDosage.getText().trim().isEmpty()
                || tfFrequency.getText().trim().isEmpty() || tfDuration.getText().trim().isEmpty()
                || tfQuantity.getText().trim().isEmpty() || tfPharmacy.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Medication/Dosage/Frequency/Duration/Quantity/Pharmacy cannot be empty.");
            return null;
        }

        Patient patient = patients.get(cbPatient.getSelectedIndex());
        Clinician clinician = clinicians.get(cbClinician.getSelectedIndex());

        return prescriptionController.createAndPersist(
                patient,
                clinician,
                tfAppointmentId.getText().trim(),
                tfMedication.getText().trim(),
                tfDosage.getText().trim(),
                tfFrequency.getText().trim(),
                tfDuration.getText().trim(),
                tfQuantity.getText().trim(),
                taInstructions.getText().trim(),
                tfPharmacy.getText().trim(),
                (String) cbStatus.getSelectedItem(),
                tfCollectionDate.getText().trim()
        );
    }
}
