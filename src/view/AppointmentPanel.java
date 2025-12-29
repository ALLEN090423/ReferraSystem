package view;

import controller.AppointmentController;
import model.Appointment;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing panel for displaying appointments.
 * Shows core fields in JTable and full CSV row in "View Details" dialog.
 */
public class AppointmentPanel extends JPanel {

    private final AppointmentController appointmentController;
    private final AppointmentTableModel tableModel;
    private final JTable table;

    public AppointmentPanel(AppointmentController appointmentController) {
        this.appointmentController = appointmentController;
        this.tableModel = new AppointmentTableModel();
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
                    "Appointment Details - " + id,
                    appointmentController.getDetailsById(id)
            );
        });
    }

    private void refreshTable() {
        tableModel.setData(appointmentController.getAll());
    }

    private static class AppointmentTableModel extends AbstractTableModel {
        private final String[] cols = {"Appointment ID", "DateTime", "Status", "Patient ID", "Clinician ID", "Facility ID"};
        private List<Appointment> data = new ArrayList<>();

        void setData(List<Appointment> list) {
            data = list == null ? new ArrayList<>() : list;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int r, int c) {
            Appointment a = data.get(r);
            return switch (c) {
                case 0 -> a.getAppointmentId();
                case 1 -> a.getDateTime();
                case 2 -> a.getStatus();
                case 3 -> a.getPatientId();
                case 4 -> a.getClinicianId();
                case 5 -> a.getFacilityId();
                default -> "";
            };
        }
    }
}
