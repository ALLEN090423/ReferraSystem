package view;

import controller.ReferralController;
import model.Clinician;
import model.Patient;
import model.Referral;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReferralDialog {

    public static Referral showCreateDialog(Component parent,
                                            List<Patient> patients,
                                            List<Clinician> clinicians,
                                            ReferralController referralController) throws Exception {

        JComboBox<String> cbPatient = new JComboBox<>();
        for (Patient p : patients) cbPatient.addItem(p.getUserId() + " - " + p.getName());

        JComboBox<String> cbClinician = new JComboBox<>();
        for (Clinician c : clinicians) cbClinician.addItem(c.getUserId() + " - " + c.getName() + " (" + c.getRole() + ")");

        JTextField tfReferredToClinicianId = new JTextField(12);
        JTextField tfRefFacilityId = new JTextField(12);
        JTextField tfToFacilityId = new JTextField(12);

        JComboBox<String> cbUrgency = new JComboBox<>(new String[]{"Routine", "Urgent", "Emergency"});

        JTextField tfReason = new JTextField(20);
        JTextArea taSummary = new JTextArea(6, 25);
        JTextField tfInvestigations = new JTextField(20); // 用 | 分隔
        JTextField tfAppointmentId = new JTextField(12);
        JTextField tfNotes = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.add(new JLabel("Patient:")); panel.add(cbPatient);
        panel.add(new JLabel("Referring Clinician:")); panel.add(cbClinician);
        panel.add(new JLabel("Referred To Clinician ID:")); panel.add(tfReferredToClinicianId);
        panel.add(new JLabel("Referring Facility ID:")); panel.add(tfRefFacilityId);
        panel.add(new JLabel("Referred To Facility ID:")); panel.add(tfToFacilityId);
        panel.add(new JLabel("Urgency:")); panel.add(cbUrgency);
        panel.add(new JLabel("Referral Reason:")); panel.add(tfReason);
        panel.add(new JLabel("Clinical Summary:")); panel.add(new JScrollPane(taSummary));
        panel.add(new JLabel("Investigations (use |):")); panel.add(tfInvestigations);
        panel.add(new JLabel("Appointment ID (optional):")); panel.add(tfAppointmentId);
        panel.add(new JLabel("Notes (optional):")); panel.add(tfNotes);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 600)); // 控制弹窗大小

        int ok = JOptionPane.showConfirmDialog(
                parent,
                scrollPane,
                "Create Referral",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (ok != JOptionPane.OK_OPTION) return null;

        // 取选中对象
        Patient p = patients.get(cbPatient.getSelectedIndex());
        Clinician c = clinicians.get(cbClinician.getSelectedIndex());

        // 基础校验
        if (tfRefFacilityId.getText().trim().isEmpty() || tfToFacilityId.getText().trim().isEmpty()
                || tfReason.getText().trim().isEmpty() || taSummary.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Facility IDs / Reason / Summary cannot be empty.");
            return null;
        }

        return referralController.createReferralAndPersist(
                p,
                c,
                tfReferredToClinicianId.getText().trim(),
                tfRefFacilityId.getText().trim(),
                tfToFacilityId.getText().trim(),
                (String) cbUrgency.getSelectedItem(),
                tfReason.getText().trim(),
                taSummary.getText().trim(),
                tfInvestigations.getText().trim(),
                tfAppointmentId.getText().trim(),
                tfNotes.getText().trim()
        );
    }
}
