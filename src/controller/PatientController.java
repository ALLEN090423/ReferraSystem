package controller;

import model.Patient;
import util.CSVTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientController {
    private final List<Patient> patients = new ArrayList<>();

    public void load(String filePath) throws IOException {
        patients.clear();
        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = row.getOrDefault("patient_id", "").trim();
            String first = row.getOrDefault("first_name", "").trim();
            String last = row.getOrDefault("last_name", "").trim();
            String name = (first + " " + last).trim();

            String dob = row.getOrDefault("date_of_birth", "").trim();
            String nhs = row.getOrDefault("nhs_number", "").trim();
            String phone = row.getOrDefault("phone_number", "").trim();
            String email = row.getOrDefault("email", "").trim();
            String gp = row.getOrDefault("gp_surgery_id", "").trim();

            String password = ""; // CSV 没有就留空

            patients.add(new Patient(id, name, email, phone, password, nhs, dob, gp));
        }
    }

    public List<Patient> getAll() { return new ArrayList<>(patients); }

    // 为 GUI 准备（下一步你会用到）
    public void add(Patient p) { patients.add(p); }
    public boolean deleteById(String id) {
        return patients.removeIf(p -> p.getUserId().equalsIgnoreCase(id));
    }
}
