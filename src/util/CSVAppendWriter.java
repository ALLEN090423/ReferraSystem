package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVAppendWriter {

    public static void appendRow(String filePath, String[] row) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(toCsvLine(row));
        }
    }

    private static String toCsvLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + v + "\"" : v;
    }
}
