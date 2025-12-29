package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {

    public static void writeAll(String filePath, List<String> header, List<String[]> rows) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
            // header
            pw.println(String.join(",", header));

            // rows
            for (String[] r : rows) {
                pw.println(toCsvLine(r));
            }
        }
    }

    // 处理需要加引号的字段（包含逗号/引号/换行）
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
