package controller;

import model.Appointment;
import util.CSVTable;

import java.io.IOException;
import java.util.*;

/**
 * Controller responsible for loading appointments.
 * Caches the original CSV rows so full details can be displayed.
 */
public class AppointmentController {

    private final List<Appointment> appointments = new ArrayList<>();

    // appointment_id -> full CSV row (all columns preserved)
    private final Map<String, Map<String, String>> rawById = new HashMap<>();

    private String loadedFilePath;

    // Header order must match appointments.csv exactly
    private static final List<String> APPOINTMENT_HEADER = List.of(
            "appointment_id","patient_id","clinician_id","facility_id",
            "appointment_date","appointment_time","duration_minutes","appointment_type",
            "status","reason_for_visit","notes","created_date","last_modified"
    );

    public void load(String filePath) throws IOException {
        loadedFilePath = filePath;
        appointments.clear();
        rawById.clear();

        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = safe(row.get("appointment_id"));
            String date = safe(row.get("appointment_date"));
            String time = safe(row.get("appointment_time"));
            String dateTime = (date + " " + time).trim();

            String status = safe(row.get("status"));
            String reason = safe(row.get("reason_for_visit"));
            String notes = safe(row.get("notes"));

            String patientId = safe(row.get("patient_id"));
            String clinicianId = safe(row.get("clinician_id"));
            String facilityId = safe(row.get("facility_id"));

            appointments.add(new Appointment(
                    id, dateTime, status, reason, notes,
                    patientId, clinicianId, facilityId
            ));

            rawById.put(id, new HashMap<>(row));
        }
    }

    public List<Appointment> getAll() {
        return new ArrayList<>(appointments);
    }

    /**
     * Return full CSV details for an appointment by ID.
     */
    public Map<String, String> getDetailsById(String appointmentId) {
        Map<String, String> row = rawById.get(appointmentId);
        if (row == null) return Map.of();

        Map<String, String> ordered = new LinkedHashMap<>();
        for (String col : APPOINTMENT_HEADER) {
            ordered.put(col, row.getOrDefault(col, ""));
        }
        return ordered;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
