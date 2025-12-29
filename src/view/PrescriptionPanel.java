package view;

import controller.ClinicianController;
import controller.PatientController;
import controller.PrescriptionController;
import model.Prescription;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing panel for displaying prescriptions.
 * Shows core fields in JTable and full CSV row in "View Details" dialog.
 */
public class PrescriptionPanel extends JPanel {

    private final PrescriptionController prescriptionController;
    private final PatientController patientController;
    private final ClinicianController clinicianController;

    private final PrescriptionTableModel tableModel = new PrescriptionTableModel();
    private final JTable table = new JTable(tableModel);

    public PrescriptionPanel(PrescriptionController prescriptionController,
                             PatientController patientController,
                             ClinicianController clinicianController) {
        this.prescriptionController = prescriptionController;
        this.patientController = patientController;
        this.clinicianController = clinicianController;

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add Prescription");
        JButton btnDetails = new JButton("View Details");

        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnDetails);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();

        btnRefresh.addActionListener(e -> refresh());

        btnAdd.addActionListener(e -> {
            try {
                var p = PrescriptionDialog.showAddDialog(
                        this,
                        patientController.getAll(),
                        clinicianController.getAll(),
                        prescriptionController
                );
                if (p != null) {
                    refresh();
                    JOptionPane.showMessageDialog(this,
                            "Prescription created: " + p.getPrescriptionId() +
                                    "\nSaved text: output/prescriptions/" + p.getPrescriptionId() + ".txt");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Create failed: " + ex.getMessage());
            }
        });

        btnDetails.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row.");
                return;
            }
            String rxId = (String) tableModel.getValueAt(row, 0);
            DetailsDialog.showDetails(
                    this,
                    "Prescription Details - " + rxId,
                    prescriptionController.getDetailsById(rxId)
            );
        });
    }

    private void refresh() {
        tableModel.setData(prescriptionController.getAll());
    }

    private static class PrescriptionTableModel extends AbstractTableModel {
        private final String[] cols = {"RX ID","Patient","Clinician","Medication","Dosage","Frequency","Status","Date"};
        private List<Prescription> data = new ArrayList<>();

        void setData(List<Prescription> list) {
            data = (list == null) ? new ArrayList<>() : list;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int r, int c) {
            Prescription p = data.get(r);
            return switch (c) {
                case 0 -> p.getPrescriptionId();
                case 1 -> p.getPatientId();
                case 2 -> p.getClinicianId();
                case 3 -> p.getMedicationName();
                case 4 -> p.getDosage();
                case 5 -> p.getFrequency();
                case 6 -> p.getStatus();
                case 7 -> p.getPrescriptionDate();
                default -> "";
            };
        }
    }
}
