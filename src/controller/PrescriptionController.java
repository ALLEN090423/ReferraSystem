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

public class PrescriptionController {

    private final List<Prescription> prescriptions = new ArrayList<>();
    private String filePath;

    public void load(String filePath) throws IOException {
        this.filePath = filePath;
        prescriptions.clear();

        CSVTable table = CSVTable.load(filePath);
        for (Map<String, String> row : table.getRows()) {
            prescriptions.add(new Prescription(
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
            ));
        }
    }

    public List<Prescription> getAll() {
        return new ArrayList<>(prescriptions);
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
            String collectionDate // 可空
    ) throws IOException {

        if (filePath == null || filePath.isBlank()) throw new IOException("prescriptions.csv not loaded");

        String newId = generateNextRxId();
        String today = LocalDate.now().toString();

        Prescription p = new Prescription(
                newId,
                patient == null ? "" : patient.getUserId(),
                clinician == null ? "" : clinician.getUserId(),
                safe(appointmentId),
                today,                       // prescription_date
                safe(medicationName),
                safe(dosage),
                safe(frequency),
                safe(durationDays),
                safe(quantity),
                safe(instructions),
                safe(pharmacyName),
                safe(status),
                today,                       // issue_date
                safe(collectionDate)         // collection_date
        );

        // 1) 内存加入
        prescriptions.add(p);

        // 2) 追加写回 CSV
        CSVAppendWriter.appendRow(filePath, toRow(p));

        // 3) 生成处方文本（用于 rubric：output text file content）
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
        return """
                ==============================
                NHS Prescription (Text Output)
                ==============================
                Prescription ID: %s
                Date: %s
                Status: %s

                Patient:
                - ID: %s
                - Name: %s
                - NHS Number: %s

                Prescriber:
                - Clinician ID: %s
                - Name: %s
                - Role: %s
                - Specialty: %s

                Appointment ID: %s

                Medication:
                - Name: %s
                - Dosage: %s
                - Frequency: %s
                - Duration (days): %s
                - Quantity: %s

                Instructions:
                %s

                Pharmacy:
                - %s

                Issue Date: %s
                Collection Date: %s
                """.formatted(
                pr.getPrescriptionId(),
                pr.getPrescriptionDate(),
                pr.getStatus(),
                pr.getPatientId(),
                safe(patient == null ? "" : patient.getName()),
                safe(patient == null ? "" : patient.getNhsNumber()),
                pr.getClinicianId(),
                safe(clinician == null ? "" : clinician.getName()),
                safe(clinician == null ? "" : clinician.getRole()),
                safe(clinician == null ? "" : clinician.getSpecialty()),
                pr.getAppointmentId(),
                pr.getMedicationName(),
                pr.getDosage(),
                pr.getFrequency(),
                pr.getDurationDays(),
                pr.getQuantity(),
                pr.getInstructions(),
                pr.getPharmacyName(),
                pr.getIssueDate(),
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
            String id = p.getPrescriptionId(); // RX001
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
}
