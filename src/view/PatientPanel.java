package view;

import controller.PatientController;
import model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing panel for displaying and managing patients.
 * Shows core fields in a table and full details in a separate dialog.
 */
public class PatientPanel extends JPanel {

    private final PatientController patientController;
    private final PatientTableModel tableModel;
    private final JTable table;

    public PatientPanel(PatientController patientController) {
        this.patientController = patientController;
        this.tableModel = new PatientTableModel();
        this.table = new JTable(tableModel);

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add Patient");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnDetails = new JButton("View Details");

        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnDelete);
        top.add(btnDetails);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();

        btnRefresh.addActionListener(e -> refreshTable());

        btnAdd.addActionListener(e -> {
            Patient p = PatientDialog.showAddDialog(this);
            if (p != null) {
                patientController.add(p);
                refreshTable();
                try {
                    patientController.save();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row.");
                return;
            }

            String id = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete patient " + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (patientController.deleteById(id)) {
                    refreshTable();
                    try {
                        patientController.save();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
                    }
                }
            }
        });

        btnDetails.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row.");
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            DetailsDialog.showDetails(
                    this,
                    "Patient Details - " + id,
                    patientController.getDetailsById(id)
            );
        });
    }

    private void refreshTable() {
        tableModel.setPatients(patientController.getAll());
    }

    /**
     * Table model showing only core patient fields.
     */
    private static class PatientTableModel extends AbstractTableModel {

        private final String[] columns = {
                "Patient ID", "Name", "NHS Number", "Date of Birth", "Phone", "Email", "GP Surgery"
        };

        private List<Patient> data = new ArrayList<>();

        public void setPatients(List<Patient> patients) {
            this.data = patients == null ? new ArrayList<>() : patients;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Patient p = data.get(row);
            return switch (col) {
                case 0 -> p.getUserId();
                case 1 -> p.getName();
                case 2 -> p.getNhsNumber();
                case 3 -> p.getDateOfBirth();
                case 4 -> p.getPhone();
                case 5 -> p.getEmail();
                case 6 -> p.getGpSurgery();
                default -> "";
            };
        }
    }
}
