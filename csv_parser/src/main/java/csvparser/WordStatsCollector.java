package csvparser;

import java.util.HashMap;
import java.util.Map;


public class WordStatsCollector {

    private final Map<String, Long> frequencies = new HashMap<>();
    private long totalWords = 0;

    public void addWord(String word) {
        totalWords++;
        frequencies.merge(word, 1L, Long::sum);
    }

    public WordStats getStats() {
        return new WordStats(frequencies, totalWords);
    }

    public record WordStats(Map<String, Long> frequencies, long totalWords) {
        public WordStats {
            frequencies = Map.copyOf(frequencies);
        }
    }

}