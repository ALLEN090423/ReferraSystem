package view;

import controller.PatientController;
import model.Patient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientController patientController;
    private final PatientTableModel tableModel;
    private final JTable table;

    public PatientPanel(PatientController patientController) {
        this.patientController = patientController;
        this.tableModel = new PatientTableModel();
        this.table = new JTable(tableModel);

        setLayout(new BorderLayout(10, 10));

        // 顶部按钮区
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add Patient");
        JButton btnDelete = new JButton("Delete Selected");

        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnDelete);

        // 表格区
        JScrollPane scrollPane = new JScrollPane(table);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 初次加载显示
        refreshTable();

        // 事件绑定
        btnRefresh.addActionListener(e -> refreshTable());

        btnAdd.addActionListener(e -> {
            Patient p = PatientDialog.showAddDialog(this);
            if (p != null) {
                patientController.add(p);   // ✅ 内存新增
                refreshTable();
                JOptionPane.showMessageDialog(this, "Patient added (in-memory). CSV persistence will be added in Step 5.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
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
                boolean ok = patientController.deleteById(id); // ✅ 内存删除
                refreshTable();
                if (!ok) JOptionPane.showMessageDialog(this, "Delete failed: patient not found.");
            }
        });
    }

    private void refreshTable() {
        tableModel.setPatients(patientController.getAll());
    }

    // ---------------- Table Model ----------------
    private static class PatientTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "NHS", "DOB", "Phone", "Email", "GP Surgery"};
        private List<Patient> data = new ArrayList<>();

        public void setPatients(List<Patient> patients) {
            this.data = (patients == null) ? new ArrayList<>() : patients;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Patient p = data.get(rowIndex);
            return switch (columnIndex) {
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
