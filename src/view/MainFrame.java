package view;

import controller.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 * Tabs provide access to different datasets using an MVC structure.
 */
public class MainFrame extends JFrame {

    public MainFrame(PatientController patientController,
                     ClinicianController clinicianController,
                     AppointmentController appointmentController,
                     ReferralController referralController,
                     PrescriptionController prescriptionController) {

        setTitle("Healthcare Management System - Part 2");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", new PatientPanel(patientController));
        tabs.addTab("Clinicians", new ClinicianPanel(clinicianController));
        tabs.addTab("Appointments", new AppointmentPanel(appointmentController));
        tabs.addTab("Referrals", new ReferralPanel(referralController, patientController, clinicianController));
        tabs.addTab("Prescriptions", new PrescriptionPanel(prescriptionController, patientController, clinicianController));

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
