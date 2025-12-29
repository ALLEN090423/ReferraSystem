import controller.AppointmentController;
import controller.ClinicianController;
import controller.PatientController;

public class Main {
    public static void main(String[] args) {
        try {
            PatientController pc = new PatientController();
            pc.load("data/patients.csv");
            System.out.println("Patients loaded: " + pc.getAll().size());
            if (!pc.getAll().isEmpty()) System.out.println(pc.getAll().get(0));

            ClinicianController cc = new ClinicianController();
            cc.load("data/clinicians.csv");
            System.out.println("Clinicians loaded: " + cc.getAll().size());
            if (!cc.getAll().isEmpty()) System.out.println(cc.getAll().get(0));

            AppointmentController ac = new AppointmentController();
            ac.load("data/appointments.csv");
            System.out.println("Appointments loaded: " + ac.getAll().size());
            if (!ac.getAll().isEmpty()) System.out.println(ac.getAll().get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

