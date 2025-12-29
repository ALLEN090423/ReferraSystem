package view;

import controller.ClinicianController;
import model.Clinician;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing panel for displaying clinicians.
 * Shows core fields in a table and full CSV fields in a separate details dialog.
 */
public class ClinicianPanel extends JPanel {

    private final ClinicianController clinicianController;
    private final ClinicianTableModel tableModel;
    private final JTable table;

    public ClinicianPanel(ClinicianController clinicianController) {
        this.clinicianController = clinicianController;
        this.tableModel = new ClinicianTableModel();
        this.table = new JTable(tableModel);

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDetails = new JButton("View Details");

        top.add(btnRefresh);
        top.add(btnDetails);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();

        btnRefresh.addActionListener(e -> refreshTable());

        btnDetails.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row.");
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            DetailsDialog.showDetails(
                    this,
                    "Clinician Details - " + id,
                    clinicianController.getDetailsById(id)
            );
        });
    }

    private void refreshTable() {
        tableModel.setData(clinicianController.getAll());
    }

    private static class ClinicianTableModel extends AbstractTableModel {
        private final String[] cols = {"Clinician ID", "Name", "Role", "Specialty", "Workplace Type", "Email", "Phone"};
        private List<Clinician> data = new ArrayList<>();

        void setData(List<Clinician> list) {
            data = list == null ? new ArrayList<>() : list;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int r, int c) {
            Clinician cl = data.get(r);
            return switch (c) {
                case 0 -> cl.getUserId();
                case 1 -> cl.getName();
                case 2 -> cl.getRole();
                case 3 -> cl.getSpecialty();
                case 4 -> cl.getWorkplaceType();
                case 5 -> cl.getEmail();
                case 6 -> cl.getPhone();
                default -> "";
            };
        }
    }
}
