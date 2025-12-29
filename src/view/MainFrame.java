package view;

import controller.ClinicianController;
import controller.PatientController;
import controller.ReferralController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(PatientController patientController,
                     ClinicianController clinicianController,
                     ReferralController referralController) {

        setTitle("Healthcare Management System - Part 2");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", new PatientPanel(patientController));
        tabs.addTab("Referrals", new ReferralPanel(referralController, patientController, clinicianController));

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}

