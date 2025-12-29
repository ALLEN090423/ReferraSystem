package model;

public class Clinician extends User {
    private String registrationNo; // gmc_number
    private String specialty;      // speciality
    private String role;           // title (GP/Nurse/Specialist)
    private String workplaceId;
    private String workplaceType;

    public Clinician(String userId, String name, String email, String phone, String password,
                     String registrationNo, String specialty, String role,
                     String workplaceId, String workplaceType) {
        super(userId, name, email, phone, password);
        this.registrationNo = registrationNo;
        this.specialty = specialty;
        this.role = role;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
    }

    public String getRegistrationNo() { return registrationNo; }
    public String getSpecialty() { return specialty; }
    public String getRole() { return role; }
    public String getWorkplaceId() { return workplaceId; }
    public String getWorkplaceType() { return workplaceType; }

    @Override
    public String toString() {
        return "Clinician{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", specialty='" + specialty + '\'' +
                ", workplaceType='" + workplaceType + '\'' +
                '}';
    }
}
