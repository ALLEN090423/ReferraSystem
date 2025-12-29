package view;

import controller.ClinicianController;
import controller.PatientController;
import controller.ReferralController;
import model.Clinician;
import model.Patient;
import model.Referral;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReferralPanel extends JPanel {

    private final ReferralController referralController;
    private final PatientController patientController;
    private final ClinicianController clinicianController;

    private final ReferralTableModel tableModel = new ReferralTableModel();
    private final JTable table = new JTable(tableModel);

    public ReferralPanel(ReferralController referralController,
                         PatientController patientController,
                         ClinicianController clinicianController) {
        this.referralController = referralController;
        this.patientController = patientController;
        this.clinicianController = clinicianController;

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnCreate = new JButton("Create Referral");

        top.add(btnRefresh);
        top.add(btnCreate);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();

        btnRefresh.addActionListener(e -> refresh());

        btnCreate.addActionListener(e -> {
            try {
                Referral r = ReferralDialog.showCreateDialog(
                        this,
                        patientController.getAll(),
                        clinicianController.getAll(),
                        referralController
                );
                if (r != null) {
                    refresh();
                    JOptionPane.showMessageDialog(this,
                            "Referral created: " + r.getReferralId() +
                                    "\nSaved email text: output/referrals/" + r.getReferralId() + ".txt");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Create failed: " + ex.getMessage());
            }
        });
    }

    private void refresh() {
        tableModel.setData(referralController.getAll());
    }

    // ------- TableModel -------
    private static class ReferralTableModel extends AbstractTableModel {
        private final String[] cols = {"ReferralID","PatientID","RefClinician","ToFacility","Urgency","Status","Date"};
        private List<Referral> data = new ArrayList<>();

        void setData(List<Referral> list) {
            data = (list == null) ? new ArrayList<>() : list;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Referral r = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getReferralId();
                case 1 -> r.getPatientId();
                case 2 -> r.getReferringClinicianId();
                case 3 -> r.getReferredToFacilityId();
                case 4 -> r.getUrgencyLevel();
                case 5 -> r.getStatus();
                case 6 -> r.getReferralDate();
                default -> "";
            };
        }
    }
}
