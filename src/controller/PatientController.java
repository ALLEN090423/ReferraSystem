package controller;

import model.Patient;
import util.CSVTable;
import util.CSVWriter;

import java.io.IOException;
import java.util.*;

public class PatientController {
    private final List<Patient> patients = new ArrayList<>();
    private String loadedFilePath; // 记住从哪个文件加载

    // 你 CSV 的固定表头（你已经给过）
    private static final List<String> PATIENT_HEADER = List.of(
            "patient_id","first_name","last_name","date_of_birth","nhs_number","gender",
            "phone_number","email","address","postcode","emergency_contact_name",
            "emergency_contact_phone","registration_date","gp_surgery_id"
    );

    public void load(String filePath) throws IOException {
        loadedFilePath = filePath;
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

            String password = "";
            patients.add(new Patient(id, name, email, phone, password, nhs, dob, gp));
        }
    }

    public List<Patient> getAll() { return new ArrayList<>(patients); }

    public void add(Patient p) {
        patients.add(p);
    }

    public boolean deleteById(String id) {
        return patients.removeIf(p -> p.getUserId().equalsIgnoreCase(id));
    }

    // ✅ 保存回 patients.csv（覆盖写）
    public void save() throws IOException {
        if (loadedFilePath == null || loadedFilePath.isBlank()) {
            throw new IOException("No file loaded yet, cannot save.");
        }

        List<String[]> rows = new ArrayList<>();
        for (Patient p : patients) {
            // name 拆成 first/last（简单拆：第一个词当 first，其余当 last）
            String fullName = p.getName() == null ? "" : p.getName().trim();
            String first = "";
            String last = "";
            if (!fullName.isEmpty()) {
                String[] parts = fullName.split("\\s+");
                first = parts[0];
                if (parts.length > 1) {
                    last = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                }
            }

            // 你 Patient model 目前没包含 gender/address/...，这里先留空
            // 后面 Step 5.5 可以扩展 Patient 字段再填满
            String[] r = new String[] {
                    p.getUserId(),             // patient_id
                    first,                     // first_name
                    last,                      // last_name
                    nullToEmpty(p.getDateOfBirth()), // date_of_birth
                    nullToEmpty(p.getNhsNumber()),   // nhs_number
                    "",                        // gender
                    nullToEmpty(p.getPhone()), // phone_number
                    nullToEmpty(p.getEmail()), // email
                    "",                        // address
                    "",                        // postcode
                    "",                        // emergency_contact_name
                    "",                        // emergency_contact_phone
                    "",                        // registration_date
                    nullToEmpty(p.getGpSurgery()) // gp_surgery_id
            };
            rows.add(r);
        }

        CSVWriter.writeAll(loadedFilePath, PATIENT_HEADER, rows);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
