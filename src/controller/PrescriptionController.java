package controller;

import model.Clinician;
import model.Patient;
import model.Prescription;
import util.CSVAppendWriter;
import util.CSVTable;
import util.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller responsible for loading and creating prescriptions.
 * It also caches raw CSV rows so full details can be displayed.
 */
public class PrescriptionController {

    private final List<Prescription> prescriptions = new ArrayList<>();

    // prescription_id -> full CSV row (all columns preserved)
    private final Map<String, Map<String, String>> rawById = new HashMap<>();

    private String filePath;

    // Header order must match prescriptions.csv exactly
    private static final List<String> PRESCRIPTION_HEADER = List.of(
            "prescription_id","patient_id","clinician_id","appointment_id","prescription_date",
            "medication_name","dosage","frequency","duration_days","quantity","instructions",
            "pharmacy_name","status","issue_date","collection_date"
    );

    public void load(String filePath) throws IOException {
        this.filePath = filePath;
        prescriptions.clear();
        rawById.clear();

        CSVTable table = CSVTable.load(filePath);
        for (Map<String, String> row : table.getRows()) {
            Prescription p = new Prescription(
                    get(row, "prescription_id"),
                    get(row, "patient_id"),
                    get(row, "clinician_id"),
                    get(row, "appointment_id"),
                    get(row, "prescription_date"),
                    get(row, "medication_name"),
                    get(row, "dosage"),
                    get(row, "frequency"),
                    get(row, "duration_days"),
                    get(row, "quantity"),
                    get(row, "instructions"),
                    get(row, "pharmacy_name"),
                    get(row, "status"),
                    get(row, "issue_date"),
                    get(row, "collection_date")
            );
            prescriptions.add(p);
            rawById.put(p.getPrescriptionId(), new HashMap<>(row));
        }
    }

    public List<Prescription> getAll() {
        return new ArrayList<>(prescriptions);
    }

    /**
     * Return full CSV details for a prescription by ID.
     */
    public Map<String, String> getDetailsById(String rxId) {
        Map<String, String> row = rawById.get(rxId);
        if (row == null) return Map.of();

        Map<String, String> ordered = new LinkedHashMap<>();
        for (String col : PRESCRIPTION_HEADER) {
            ordered.put(col, row.getOrDefault(col, ""));
        }
        return ordered;
    }

    public Prescription createAndPersist(
            Patient patient,
            Clinician clinician,
            String appointmentId,
            String medicationName,
            String dosage,
            String frequency,
            String durationDays,
            String quantity,
            String instructions,
            String pharmacyName,
            String status,
            String collectionDate
    ) throws IOException {

        if (filePath == null || filePath.isBlank()) throw new IOException("prescriptions.csv not loaded");

        String newId = generateNextRxId();
        String today = LocalDate.now().toString();

        Prescription p = new Prescription(
                newId,
                patient == null ? "" : patient.getUserId(),
                clinician == null ? "" : clinician.getUserId(),
                safe(appointmentId),
                today,
                safe(medicationName),
                safe(dosage),
                safe(frequency),
                safe(durationDays),
                safe(quantity),
                safe(instructions),
                safe(pharmacyName),
                safe(status),
                today,
                safe(collectionDate)
        );

        // 1) In-memory list
        prescriptions.add(p);

        // 2) Append to CSV
        String[] row = toRow(p);
        CSVAppendWriter.appendRow(filePath, row);

        // 3) Cache raw row for details
        Map<String, String> raw = new HashMap<>();
        for (int i = 0; i < PRESCRIPTION_HEADER.size(); i++) {
            raw.put(PRESCRIPTION_HEADER.get(i), row[i] == null ? "" : row[i]);
        }
        rawById.put(p.getPrescriptionId(), raw);

        // 4) Output text file for marking
        String text = generatePrescriptionText(p, patient, clinician);
        savePrescriptionTextFile(p, text);

        return p;
    }

    private String[] toRow(Prescription p) {
        return new String[] {
                p.getPrescriptionId(),
                p.getPatientId(),
                p.getClinicianId(),
                p.getAppointmentId(),
                p.getPrescriptionDate(),
                p.getMedicationName(),
                p.getDosage(),
                p.getFrequency(),
                p.getDurationDays(),
                p.getQuantity(),
                p.getInstructions(),
                p.getPharmacyName(),
                p.getStatus(),
                p.getIssueDate(),
                p.getCollectionDate()
        };
    }

    public String generatePrescriptionText(Prescription pr, Patient patient, Clinician clinician) {
        String patientName = safe(patient == null ? "" : patient.getName());
        String nhs = safe(patient == null ? "" : patient.getNhsNumber());
        String clinicianName = safe(clinician == null ? "" : clinician.getName());
        String clinicianRole = safe(clinician == null ? "" : clinician.getRole());
        String clinicianSpec = safe(clinician == null ? "" : clinician.getSpecialty());

        return """
            ==================================================
            NHS Prescription (Text Output)
            ==================================================
            Prescription ID: %s
            Issue Date: %s
            Prescription Date: %s
            Status: %s

            -------------------- Patient ---------------------
            Patient ID: %s
            Name: %s
            NHS Number: %s

            ------------------- Prescriber -------------------
            Clinician ID: %s
            Name: %s
            Role: %s
            Specialty: %s

            Appointment ID: %s

            ------------------- Medication -------------------
            Medication Name: %s
            Dosage: %s
            Frequency: %s
            Duration (days): %s
            Quantity: %s

            Instructions:
            %s

            -------------------- Pharmacy --------------------
            Pharmacy: %s

            Collection Date: %s

            --------------------- Notice ---------------------
            This is a simulated prescription output for coursework.
            No real prescription was issued. Generated by the system.
            """.formatted(
                pr.getPrescriptionId(),
                pr.getIssueDate(),
                pr.getPrescriptionDate(),
                pr.getStatus(),
                pr.getPatientId(),
                patientName,
                nhs,
                pr.getClinicianId(),
                clinicianName,
                clinicianRole,
                clinicianSpec,
                pr.getAppointmentId(),
                pr.getMedicationName(),
                pr.getDosage(),
                pr.getFrequency(),
                pr.getDurationDays(),
                pr.getQuantity(),
                pr.getInstructions(),
                pr.getPharmacyName(),
                pr.getCollectionDate()
        );
    }


    public void savePrescriptionTextFile(Prescription p, String content) throws IOException {
        String dir = "output/prescriptions";
        String path = dir + "/" + p.getPrescriptionId() + ".txt";
        FileUtil.writeText(path, content);
    }

    private String generateNextRxId() {
        int max = 0;
        for (Prescription p : prescriptions) {
            String id = p.getPrescriptionId();
            if (id != null && id.startsWith("RX")) {
                String num = id.substring(2).replaceAll("\\D", "");
                if (!num.isEmpty()) max = Math.max(max, Integer.parseInt(num));
            }
        }
        return String.format("RX%03d", max + 1);
    }

    private String get(Map<String, String> row, String key) {
        String v = row.get(key);
        return v == null ? "" : v.trim();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Delete a prescription by ID and rewrite the CSV file.
     */
    public boolean deleteById(String rxId) throws IOException {
        if (rxId == null || rxId.isBlank()) return false;

        boolean removed = prescriptions.removeIf(p -> p.getPrescriptionId().equalsIgnoreCase(rxId));
        if (!removed) return false;

        rawById.remove(rxId);

        rewriteCsv(); // Persist deletion
        return true;
    }

    /**
     * Rewrite prescriptions.csv using current in-memory list while preserving column order.
     */
    private void rewriteCsv() throws IOException {
        if (filePath == null || filePath.isBlank()) throw new IOException("prescriptions.csv not loaded");

        List<String[]> rows = new ArrayList<>();
        for (Prescription p : prescriptions) {
            rows.add(toRow(p));
        }

        // CSVWriter expects List<String> header
        util.CSVWriter.writeAll(filePath, PRESCRIPTION_HEADER, rows);
    }

}
