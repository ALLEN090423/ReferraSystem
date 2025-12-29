package controller;

import model.Appointment;
import util.CSVTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppointmentController {
    private final List<Appointment> appointments = new ArrayList<>();

    public void load(String filePath) throws IOException {
        appointments.clear();
        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = row.getOrDefault("appointment_id", "").trim();

            String date = row.getOrDefault("appointment_date", "").trim();
            String time = row.getOrDefault("appointment_time", "").trim();
            String dateTime = (date + " " + time).trim(); // ✅ 合并

            String status = row.getOrDefault("status", "").trim();
            String reason = row.getOrDefault("reason_for_visit", "").trim();
            String notes = row.getOrDefault("notes", "").trim();

            String patientId = row.getOrDefault("patient_id", "").trim();
            String clinicianId = row.getOrDefault("clinician_id", "").trim();
            String facilityId = row.getOrDefault("facility_id", "").trim();

            appointments.add(new Appointment(id, dateTime, status, reason, notes, patientId, clinicianId, facilityId));
        }
    }

    public List<Appointment> getAll() { return new ArrayList<>(appointments); }
}
