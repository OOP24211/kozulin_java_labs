package csvparser;

import csvparser.Validators.CliValidator;
import csvparser.Validators.ValidatorErrors;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {


        CliValidator cliValidator = new CliValidator();
        ValidatorErrors validator = cliValidator.validateArgs(args);

        if (validator.hasErrors()) {
            for (String error : validator.errors()) {
                System.err.println(error);
            }
            return;
        }
        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);


        WordExtractor extractor = new WordExtractor();
        WordStatsCollector collector = new WordStatsCollector();
        CsvExporter exporter = new CsvExporter();

        extractor.extractWords(input, collector::addWord);
        WordStatsCollector.WordStats stats = collector.getStats();
        exporter.exportToCsv(stats, output);

        System.out.println("Результат сохранён в файл: " + output.toAbsolutePath());
    }
}

