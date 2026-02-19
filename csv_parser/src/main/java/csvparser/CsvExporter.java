package csvparser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

public class CsvExporter {
    private static final String CSV_HEADER = "Слово,Количество,Процент";
    public void exportToCsv(WordStatsCollector.WordStats stats, Path output) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                output,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {


            writer.write(CSV_HEADER);
            writer.newLine();

            long total = stats.totalWords();

            stats.frequencies().entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                            .reversed()
                            .thenComparing(Map.Entry::getKey))
                    .forEach(entry -> {

                        try {
                            double pct = total == 0 ? 0.0 : (entry.getValue() * 100.0 / total);
                            String word = entry.getKey();
                            long count = entry.getValue();
                            writer.write(String.format(Locale.US, "%s,%d,%.3f", word, count, pct));
                            writer.newLine();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }
    }
}
