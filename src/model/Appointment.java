package model;

public class Appointment {
    private String appointmentId;
    private String dateTime;      // "2025-09-20 09:00"
    private String status;
    private String reason;
    private String notes;

    private String patientId;
    private String clinicianId;
    private String facilityId;

    public Appointment(String appointmentId, String dateTime, String status, String reason, String notes,
                       String patientId, String clinicianId, String facilityId) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.status = status;
        this.reason = reason;
        this.notes = notes;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public String getNotes() { return notes; }
    public String getPatientId() { return patientId; }
    public String getClinicianId() { return clinicianId; }
    public String getFacilityId() { return facilityId; }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", status='" + status + '\'' +
                ", patientId='" + patientId + '\'' +
                ", clinicianId='" + clinicianId + '\'' +
                ", facilityId='" + facilityId + '\'' +
                '}';
    }
}

