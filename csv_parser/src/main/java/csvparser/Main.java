package csvparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Использование: <Текстовый файл на ввод> <Файл, куда сохранять результат .csv>");
            return;
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);

        if (!Files.isRegularFile(input)) {
            System.err.println("Ошибка открытия файла: " + input);
            return;
        }

        WordExtractor extractor = new WordExtractor();
        WordStatsCollector collector = new WordStatsCollector();
        CsvExporter exporter = new CsvExporter();

        extractor.extractWords(input, collector::addWord);
        WordStatsCollector.WordStats stats = collector.getStats();
        exporter.exportToCsv(stats, output);

        System.out.println("Результат сохранён в файл: " + output.toAbsolutePath());
    }
}
