package model;

public class Patient extends User {
    private String nhsNumber;
    private String dateOfBirth;
    private String gpSurgery; // 对齐你 Part1 “registered GP surgery / facility”

    public Patient(String userId, String name, String email, String phone, String password,
                   String nhsNumber, String dateOfBirth, String gpSurgery) {
        super(userId, name, email, phone, password);
        this.nhsNumber = nhsNumber;
        this.dateOfBirth = dateOfBirth;
        this.gpSurgery = gpSurgery;
    }

    public String getNhsNumber() { return nhsNumber; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGpSurgery() { return gpSurgery; }

    public void setNhsNumber(String nhsNumber) { this.nhsNumber = nhsNumber; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGpSurgery(String gpSurgery) { this.gpSurgery = gpSurgery; }

    @Override
    public String toString() {
        return "Patient{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", nhsNumber='" + nhsNumber + '\'' +
                '}';
    }
}
