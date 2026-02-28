package csvparser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CsvExporter {
    private static final String CSV_HEADER = "Слово,Количество,Процент";
    public void exportToCsv(WordStatsCollector.WordStats stats, Path output) throws IOException {
            long total = stats.totalWords();
            List<String> lines = new ArrayList<>();
            lines.add(CSV_HEADER);
            stats.frequencies().entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                            .reversed()
                            .thenComparing(Map.Entry::getKey))
                    .forEach(entry -> {
                                double pct = total == 0 ? 0.0 : (entry.getValue() * 100.0 / total);
                                lines.add(String.format(Locale.US, "%s,%d,%.3f", entry.getKey(), entry.getValue(),pct));
                            });
                    Files.write(output,lines,StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }


