package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileUtil {

    public static void ensureDir(String dirPath) throws IOException {
        Path p = Paths.get(dirPath);
        if (!Files.exists(p)) Files.createDirectories(p);
    }

    public static void writeText(String filePath, String content) throws IOException {
        Path p = Paths.get(filePath);
        ensureDir(p.getParent().toString());
        Files.writeString(p, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
