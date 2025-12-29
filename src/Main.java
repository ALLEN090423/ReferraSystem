import util.CSVReader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<String[]> patients = CSVReader.readAll("data/patients.csv", true);
            System.out.println("Loaded patients: " + patients.size());

            // print first row to verify
            if (!patients.isEmpty()) {
                System.out.println("First patient row fields: " + patients.get(0).length);
                System.out.println("First field: " + patients.get(0)[0]);
            }
        } catch (Exception e) {
            System.out.println("Failed to load CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

