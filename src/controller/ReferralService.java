package controller;

import model.Clinician;
import model.Patient;
import model.Referral;
import util.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;

public class ReferralService {

    private static ReferralService instance; // ✅ Singleton
    private final Deque<Referral> referralQueue = new ArrayDeque<>();

    private ReferralService() {}

    public static ReferralService getInstance() {
        if (instance == null) instance = new ReferralService();
        return instance;
    }

    // 管理队列（题目提到 referral queues）
    public void enqueue(Referral r) {
        referralQueue.addLast(r);
    }

    public Referral dequeue() {
        return referralQueue.pollFirst();
    }

    // 生成“邮件文本”（题目提到 email communications）
    public String generateReferralEmailText(Referral r, Patient p, Clinician referringClinician) {
        return """
                ==============================
                NHS Referral (Simulated Email)
                ==============================
                Referral ID: %s
                Date: %s
                Urgency: %s

                Patient:
                - ID: %s
                - Name: %s
                - NHS Number: %s
                - DOB: %s
                - Contact: %s / %s

                Referring Clinician:
                - ID: %s
                - Name: %s
                - Role: %s
                - Specialty: %s

                Referral Details:
                - From Facility ID: %s
                - To Facility ID: %s
                - Referred To Clinician ID: %s
                - Appointment ID: %s

                Reason for Referral:
                %s

                Clinical Summary:
                %s

                Requested Investigations:
                %s

                Notes:
                %s

                Status: %s
                Last Updated: %s
                """.formatted(
                r.getReferralId(),
                r.getReferralDate(),
                r.getUrgencyLevel(),
                r.getPatientId(),
                safe(p == null ? "" : p.getName()),
                safe(p == null ? "" : p.getNhsNumber()),
                safe(p == null ? "" : p.getDateOfBirth()),
                safe(p == null ? "" : p.getPhone()),
                safe(p == null ? "" : p.getEmail()),
                r.getReferringClinicianId(),
                safe(referringClinician == null ? "" : referringClinician.getName()),
                safe(referringClinician == null ? "" : referringClinician.getRole()),
                safe(referringClinician == null ? "" : referringClinician.getSpecialty()),
                r.getReferringFacilityId(),
                r.getReferredToFacilityId(),
                r.getReferredToClinicianId(),
                r.getAppointmentId(),
                r.getReferralReason(),
                r.getClinicalSummary(),
                r.getRequestedInvestigations(),
                r.getNotes(),
                r.getStatus(),
                r.getLastUpdated()
        );
    }

    // 写入文本文件（模拟邮件落盘）
    public void saveReferralEmailToFile(Referral r, String emailText) throws IOException {
        String dir = "output/referrals";
        String path = dir + "/" + r.getReferralId() + ".txt";
        FileUtil.writeText(path, emailText);
    }

    // 模拟 EHR 更新（题目提到 EHR updates）
    public void updateEhrAuditTrail(Referral r) throws IOException {
        // 简化：写一条审计日志
        String dir = "output/audit";
        String path = dir + "/ehr_audit.log";
        String line = LocalDate.now() + " - EHR updated for referral " + r.getReferralId() + " (patient " + r.getPatientId() + ")\n";
        FileUtil.ensureDir(dir);
        java.nio.file.Files.writeString(java.nio.file.Paths.get(path), line,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
