package controller;

import model.Clinician;
import util.CSVTable;

import java.io.IOException;
import java.util.*;

/**
 * Controller responsible for loading and providing access to clinician data.
 * It caches the original CSV rows so full details can be shown in a "View Details" dialog.
 */
public class ClinicianController {

    private final List<Clinician> clinicians = new ArrayList<>();

    // clinician_id -> full CSV row (all columns preserved)
    private final Map<String, Map<String, String>> rawById = new HashMap<>();

    private String loadedFilePath;

    // Header order must match clinicians.csv exactly
    private static final List<String> CLINICIAN_HEADER = List.of(
            "clinician_id","first_name","last_name","title","speciality","gmc_number",
            "phone_number","email","workplace_id","workplace_type","employment_status","start_date"
    );

    /**
     * Load clinicians from CSV file.
     */
    public void load(String filePath) throws IOException {
        loadedFilePath = filePath;
        clinicians.clear();
        rawById.clear();

        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = safe(row.get("clinician_id"));
            String first = safe(row.get("first_name"));
            String last = safe(row.get("last_name"));
            String name = (first + " " + last).trim();

            String role = safe(row.get("title"));          // GP / Nurse / Specialist
            String specialty = safe(row.get("speciality")); // e.g., General Practice
            String reg = safe(row.get("gmc_number"));

            String phone = safe(row.get("phone_number"));
            String email = safe(row.get("email"));
            String workplaceId = safe(row.get("workplace_id"));
            String workplaceType = safe(row.get("workplace_type"));

            clinicians.add(new Clinician(
                    id, name, email, phone, "",
                    reg, specialty, role,
                    workplaceId, workplaceType
            ));

            // Cache full CSV row for View Details
            rawById.put(id, new HashMap<>(row));
        }
    }

    public List<Clinician> getAll() {
        return new ArrayList<>(clinicians);
    }

    /**
     * Return full CSV details for a clinician by ID.
     */
    public Map<String, String> getDetailsById(String clinicianId) {
        Map<String, String> row = rawById.get(clinicianId);
        if (row == null) return Map.of();

        Map<String, String> ordered = new LinkedHashMap<>();
        for (String col : CLINICIAN_HEADER) {
            ordered.put(col, row.getOrDefault(col, ""));
        }
        return ordered;
    }

    /**
     * Find a clinician by ID (useful for prescriptions/referrals).
     */
    public Clinician findById(String id) {
        for (Clinician c : clinicians) {
            if (c.getUserId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
