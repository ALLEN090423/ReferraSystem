package view;

import controller.PatientController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(PatientController patientController) {
        setTitle("Healthcare Management System - Part 2");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", new PatientPanel(patientController));


        // tabs.addTab("Clinicians", new ClinicianPanel(...));
        // tabs.addTab("Appointments", new AppointmentPanel(...));
        // tabs.addTab("Prescriptions", new PrescriptionPanel(...));
        // tabs.addTab("Referrals", new ReferralPanel(...));

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
