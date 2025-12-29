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

        // 1) 先读一遍原 CSV（为了保留 address/postcode/emergency contact 等字段）
        CSVTable original = CSVTable.load(loadedFilePath);

        // patient_id -> 原始行
        Map<String, Map<String, String>> originalById = new HashMap<>();
        for (Map<String, String> row : original.getRows()) {
            String id = safe(row.get("patient_id"));
            if (!id.isEmpty()) originalById.put(id, row);
        }

        // 2) 构建写回 rows（按 PATIENT_HEADER 顺序输出）
        List<String[]> outRows = new ArrayList<>();

        for (Patient p : patients) {
            String id = safe(p.getUserId());

            // 基于原始行（如果存在），否则用空行
            Map<String, String> base = originalById.getOrDefault(id, new HashMap<>());

            // ---- 更新我们掌控的字段（其余字段保持 base 的值）----
            // name 拆成 first/last
            String fullName = safe(p.getName());
            String first = "";
            String last = "";
            if (!fullName.isEmpty()) {
                String[] parts = fullName.split("\\s+");
                first = parts[0];
                if (parts.length > 1) {
                    last = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                }
            }

            // 写入/覆盖这些字段
            base.put("patient_id", id);
            base.put("first_name", first);
            base.put("last_name", last);
            base.put("date_of_birth", safe(p.getDateOfBirth()));
            base.put("nhs_number", safe(p.getNhsNumber()));
            base.put("phone_number", safe(p.getPhone()));
            base.put("email", safe(p.getEmail()));
            base.put("gp_surgery_id", safe(p.getGpSurgery()));

            // 3) 按 header 输出整行（保留 base 里原本就有的 address/postcode 等）
            String[] rowArr = new String[PATIENT_HEADER.size()];
            for (int i = 0; i < PATIENT_HEADER.size(); i++) {
                String col = PATIENT_HEADER.get(i);
                rowArr[i] = safe(base.get(col));
            }
            outRows.add(rowArr);
        }

        // 4) 覆盖写回（但字段不会丢，因为我们保留了 base 里的原值）
        CSVWriter.writeAll(loadedFilePath, PATIENT_HEADER, outRows);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
