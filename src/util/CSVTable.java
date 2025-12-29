package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVTable {
    private final List<String> header;
    private final List<Map<String, String>> rows;

    private CSVTable(List<String> header, List<Map<String, String>> rows) {
        this.header = header;
        this.rows = rows;
    }

    public List<String> getHeader() { return header; }
    public List<Map<String, String>> getRows() { return rows; }

    public static CSVTable load(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("Empty CSV: " + filePath);

            String[] h = CSVReader.parseCsvLine(headerLine);
            List<String> header = new ArrayList<>();
            for (String col : h) header.add(col.trim());

            List<Map<String, String>> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] vals = CSVReader.parseCsvLine(line);

                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < header.size(); i++) {
                    String key = header.get(i);
                    String val = (i < vals.length) ? vals[i].trim() : "";
                    row.put(key, val);
                }
                rows.add(row);
            }
            return new CSVTable(header, rows);
        }
    }
}
