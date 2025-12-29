package controller;

import model.Patient;
import util.CSVTable;
import util.CSVWriter;

import java.io.IOException;
import java.util.*;

/**
 * Controller responsible for loading, managing and persisting Patient data.
 * It also keeps a copy of the original CSV rows so that full details
 * can be displayed in a "View Details" dialog.
 */
public class PatientController {

    private final List<Patient> patients = new ArrayList<>();

    // patient_id -> full CSV row (all columns preserved)
    private final Map<String, Map<String, String>> rawById = new HashMap<>();

    private String loadedFilePath;

    // Header order must match patients.csv exactly
    private static final List<String> PATIENT_HEADER = List.of(
            "patient_id","first_name","last_name","date_of_birth","nhs_number","gender",
            "phone_number","email","address","postcode","emergency_contact_name",
            "emergency_contact_phone","registration_date","gp_surgery_id"
    );

    /**
     * Load patients from CSV file.
     * Both Patient objects and raw CSV rows are cached.
     */
    public void load(String filePath) throws IOException {
        loadedFilePath = filePath;
        patients.clear();
        rawById.clear();

        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = safe(row.get("patient_id"));
            String first = safe(row.get("first_name"));
            String last = safe(row.get("last_name"));
            String name = (first + " " + last).trim();

            String dob = safe(row.get("date_of_birth"));
            String nhs = safe(row.get("nhs_number"));
            String phone = safe(row.get("phone_number"));
            String email = safe(row.get("email"));
            String gp = safe(row.get("gp_surgery_id"));

            patients.add(new Patient(id, name, email, phone, "", nhs, dob, gp));

            // Cache full CSV row for View Details
            rawById.put(id, new HashMap<>(row));
        }
    }

    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    /**
     * Add a new patient (in memory).
     * Missing CSV fields are initialised as empty.
     */
    public void add(Patient p) {
        patients.add(p);

        Map<String, String> row = new HashMap<>();
        for (String col : PATIENT_HEADER) row.put(col, "");

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

        row.put("patient_id", safe(p.getUserId()));
        row.put("first_name", first);
        row.put("last_name", last);
        row.put("date_of_birth", safe(p.getDateOfBirth()));
        row.put("nhs_number", safe(p.getNhsNumber()));
        row.put("phone_number", safe(p.getPhone()));
        row.put("email", safe(p.getEmail()));
        row.put("gp_surgery_id", safe(p.getGpSurgery()));

        rawById.put(safe(p.getUserId()), row);
    }

    /**
     * Delete patient by ID.
     */
    public boolean deleteById(String id) {
        boolean ok = patients.removeIf(p -> p.getUserId().equalsIgnoreCase(id));
        if (ok) rawById.remove(id);
        return ok;
    }

    /**
     * Return full CSV details for a patient.
     * Used by the View Details dialog.
     */
    public Map<String, String> getDetailsById(String patientId) {
        Map<String, String> row = rawById.get(patientId);
        if (row == null) return Map.of();

        Map<String, String> ordered = new LinkedHashMap<>();
        for (String col : PATIENT_HEADER) {
            ordered.put(col, row.getOrDefault(col, ""));
        }
        return ordered;
    }

    /**
     * Save all patients back to CSV while preserving
     * fields that are not part of the Patient model.
     */
    public void save() throws IOException {
        if (loadedFilePath == null || loadedFilePath.isBlank()) {
            throw new IOException("No file loaded.");
        }

        List<String[]> rows = new ArrayList<>();

        for (Patient p : patients) {
            Map<String, String> base = rawById.getOrDefault(p.getUserId(), new HashMap<>());

            String[] out = new String[PATIENT_HEADER.size()];
            for (int i = 0; i < PATIENT_HEADER.size(); i++) {
                out[i] = safe(base.get(PATIENT_HEADER.get(i)));
            }
            rows.add(out);
        }

        CSVWriter.writeAll(loadedFilePath, PATIENT_HEADER, rows);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }


    /**
     * Update an existing patient by ID.
     * Also updates cached raw CSV row for View Details and persistence.
     */
    public boolean update(Patient updated) {
        if (updated == null) return false;

        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getUserId().equalsIgnoreCase(updated.getUserId())) {
                patients.set(i, updated);

                // Update raw cache for key fields (preserve other columns)
                Map<String, String> base = rawById.getOrDefault(updated.getUserId(), new HashMap<>());
                String fullName = safe(updated.getName());

                String first = "";
                String last = "";
                if (!fullName.isEmpty()) {
                    String[] parts = fullName.split("\\s+");
                    first = parts[0];
                    if (parts.length > 1) last = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                }

                base.put("patient_id", safe(updated.getUserId()));
                base.put("first_name", first);
                base.put("last_name", last);
                base.put("date_of_birth", safe(updated.getDateOfBirth()));
                base.put("nhs_number", safe(updated.getNhsNumber()));
                base.put("phone_number", safe(updated.getPhone()));
                base.put("email", safe(updated.getEmail()));
                base.put("gp_surgery_id", safe(updated.getGpSurgery()));

                rawById.put(updated.getUserId(), base);
                return true;
            }
        }
        return false;
    }

}

