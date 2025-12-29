package util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CsvField {

    /**
     * Pick a value from a row by trying multiple aliases.
     * It tries:
     * 1) exact key
     * 2) case-insensitive match
     * 3) normalized match (remove spaces/_/-)
     * 4) "contains" match on normalized keys (helps with headers like Patient_ID, patientId, patient id)
     */
    public static String pick(Map<String, String> row, String... aliases) {
        if (row == null || row.isEmpty()) return "";

        // 1) exact key
        for (String a : aliases) {
            String v = row.get(a);
            if (notBlank(v)) return v.trim();
        }

        Set<String> keys = row.keySet();

        // 2) case-insensitive match
        for (String a : aliases) {
            for (String k : keys) {
                if (k.equalsIgnoreCase(a)) {
                    String v = row.get(k);
                    if (notBlank(v)) return v.trim();
                }
            }
        }

        // 3) normalized equals
        for (String a : aliases) {
            String na = norm(a);
            for (String k : keys) {
                if (norm(k).equals(na)) {
                    String v = row.get(k);
                    if (notBlank(v)) return v.trim();
                }
            }
        }

        // 4) normalized contains (best-effort)
        for (String a : aliases) {
            String na = norm(a);
            for (String k : keys) {
                String nk = norm(k);
                if (nk.contains(na) || na.contains(nk)) {
                    String v = row.get(k);
                    if (notBlank(v)) return v.trim();
                }
            }
        }

        return "";
    }

    /**
     * Combine first+last name if needed.
     */
    public static String pickFullName(Map<String, String> row) {
        String full = pick(row, "full_name", "fullname", "patient_name", "clinician_name", "name");
        if (notBlank(full)) return full.trim();

        String first = pick(row, "first_name", "firstname", "given_name", "forename");
        String last  = pick(row, "last_name", "lastname", "surname", "family_name");

        String combined = (first + " " + last).trim();
        return combined.isEmpty() ? "" : combined;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String norm(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT)
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
                .replace(".", "");
    }
}
