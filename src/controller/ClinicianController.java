package controller;

import model.Clinician;
import util.CSVTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinicianController {
    private final List<Clinician> clinicians = new ArrayList<>();

    public void load(String filePath) throws IOException {
        clinicians.clear();
        CSVTable table = CSVTable.load(filePath);

        for (Map<String, String> row : table.getRows()) {
            String id = row.getOrDefault("clinician_id", "").trim();
            String first = row.getOrDefault("first_name", "").trim();
            String last = row.getOrDefault("last_name", "").trim();
            String name = (first + " " + last).trim();

            String role = row.getOrDefault("title", "").trim();          // ✅ GP / Nurse / Specialist
            String specialty = row.getOrDefault("speciality", "").trim(); // ✅ General Practice
            String reg = row.getOrDefault("gmc_number", "").trim();

            String phone = row.getOrDefault("phone_number", "").trim();
            String email = row.getOrDefault("email", "").trim();
            String workplaceId = row.getOrDefault("workplace_id", "").trim();
            String workplaceType = row.getOrDefault("workplace_type", "").trim();

            String password = "";

            clinicians.add(new Clinician(id, name, email, phone, password, reg, specialty, role, workplaceId, workplaceType));
        }
    }

    public List<Clinician> getAll() { return new ArrayList<>(clinicians); }
}
