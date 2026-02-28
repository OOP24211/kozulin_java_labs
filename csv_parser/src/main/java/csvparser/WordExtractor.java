package csvparser;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractor {

    private static final Pattern WORD_PATTERN =
            Pattern.compile("[\\p{L}]+(?:['-][\\p{L}]+)*");

    public void extractWords(Path input, Consumer<String> wordConsumer) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = WORD_PATTERN.matcher(line.toLowerCase(Locale.ROOT));
                while (matcher.find()) {
                    wordConsumer.accept(matcher.group());
                }
            }
        }
    }
}

