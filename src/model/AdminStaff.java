package model;

public class AdminStaff extends User {

    public AdminStaff(String userId, String name, String email, String phone, String password) {
        super(userId, name, email, phone, password);
    }

    // 对齐类图：register_patient / manage_schedule（占位即可）
    public void registerPatient() { /* placeholder */ }
    public void manageSchedule() { /* placeholder */ }

    @Override
    public String toString() {
        return "AdminStaff{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
