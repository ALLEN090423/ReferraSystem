package controller;

import model.Clinician;
import model.Patient;
import model.Referral;
import util.CSVAppendWriter;
import util.CSVTable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ReferralController {

    private final List<Referral> referrals = new ArrayList<>();
    private String filePath;

    // 固定表头顺序（必须和老师 CSV 一致）
    private static final String[] REF_HEADER = new String[] {
            "referral_id","patient_id","referring_clinician_id","referred_to_clinician_id",
            "referring_facility_id","referred_to_facility_id","referral_date","urgency_level",
            "referral_reason","clinical_summary","requested_investigations","status",
            "appointment_id","notes","created_date","last_updated"
    };

    public void load(String filePath) throws IOException {
        this.filePath = filePath;
        referrals.clear();
        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            referrals.add(new Referral(
                    get(row, "referral_id"),
                    get(row, "patient_id"),
                    get(row, "referring_clinician_id"),
                    get(row, "referred_to_clinician_id"),
                    get(row, "referring_facility_id"),
                    get(row, "referred_to_facility_id"),
                    get(row, "referral_date"),
                    get(row, "urgency_level"),
                    get(row, "referral_reason"),
                    get(row, "clinical_summary"),
                    get(row, "requested_investigations"),
                    get(row, "status"),
                    get(row, "appointment_id"),
                    get(row, "notes"),
                    get(row, "created_date"),
                    get(row, "last_updated")
            ));
        }
    }

    public List<Referral> getAll() { return new ArrayList<>(referrals); }

    public Referral createReferralAndPersist(
            Patient patient,
            Clinician referringClinician,
            String referredToClinicianId,
            String referringFacilityId,
            String referredToFacilityId,
            String urgencyLevel,
            String referralReason,
            String clinicalSummary,
            String requestedInvestigations,
            String appointmentId,
            String notes
    ) throws IOException {

        if (filePath == null || filePath.isBlank()) throw new IOException("referrals.csv not loaded");

        String newId = generateNextReferralId();
        String today = LocalDate.now().toString();

        Referral r = new Referral(
                newId,
                patient == null ? "" : patient.getUserId(),
                referringClinician == null ? "" : referringClinician.getUserId(),
                safe(referredToClinicianId),
                safe(referringFacilityId),
                safe(referredToFacilityId),
                today,
                safe(urgencyLevel),
                safe(referralReason),
                safe(clinicalSummary),
                safe(requestedInvestigations),
                "Pending",
                safe(appointmentId),
                safe(notes),
                today,
                today
        );

        // 1) 内存加入
        referrals.add(r);

        // 2) 追加写入 CSV
        String[] row = toRow(r);
        CSVAppendWriter.appendRow(filePath, row);

        // 3) 调用 Singleton：队列 + 生成邮件文本 + 保存文件 + 审计日志
        ReferralService svc = ReferralService.getInstance();
        svc.enqueue(r);
        String emailText = svc.generateReferralEmailText(r, patient, referringClinician);
        svc.saveReferralEmailToFile(r, emailText);
        svc.updateEhrAuditTrail(r);

        return r;
    }

    private String[] toRow(Referral r) {
        return new String[] {
                r.getReferralId(),
                r.getPatientId(),
                r.getReferringClinicianId(),
                r.getReferredToClinicianId(),
                r.getReferringFacilityId(),
                r.getReferredToFacilityId(),
                r.getReferralDate(),
                r.getUrgencyLevel(),
                r.getReferralReason(),
                r.getClinicalSummary(),
                r.getRequestedInvestigations(),
                r.getStatus(),
                r.getAppointmentId(),
                r.getNotes(),
                r.getCreatedDate(),
                r.getLastUpdated()
        };
    }

    private String generateNextReferralId() {
        int max = 0;
        for (Referral r : referrals) {
            String id = r.getReferralId(); // e.g. R001
            if (id != null && id.startsWith("R")) {
                String num = id.substring(1).replaceAll("\\D", "");
                if (!num.isEmpty()) {
                    max = Math.max(max, Integer.parseInt(num));
                }
            }
        }
        return String.format("R%03d", max + 1);
    }

    private String get(Map<String, String> row, String key) {
        String v = row.get(key);
        return v == null ? "" : v.trim();
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
