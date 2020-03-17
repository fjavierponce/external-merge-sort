package fponce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
	// External sorting used to manage data that does not fit in main memory
        // 1. Read pages/chunks of data
        // 2. Sort using merge sort
        // 3. Merge the sorted chunks into one

        // Sorting files:
        // 1. Read chunks of files (small enough to fit in memory), files are already sorted
        // 2. Sort them
        // 3. Write to file
        String[] fileNames = new String[]{"serverA.log", "serverB.log"};
        merge(fileNames);
    }


    static void merge(String[] fileNames) {
        Map<String, String> chunks = new HashMap<>();
        Map<String, Integer> pointers = new HashMap<>();
        for(int i = 0; i < fileNames.length; i++) {
            pointers.put(fileNames[i], -1);
        }

        for(int i = 0; i < fileNames.length; i++) {
            chunks.put(fileNames[i], readLine(fileNames[i], pointers.get(fileNames[i]) + 1));
            pointers.replace(fileNames[i], pointers.get(fileNames[i]) + 1);
        }

        String line;
        while (!chunks.isEmpty()) {
            String minLogLine = getMinLogLine(chunks);
            writeAndRemoveLogLine(minLogLine, chunks);
            int pointer = pointers.get(minLogLine);
            line = readLine(minLogLine, pointer+1);
            if(line != null) {
                chunks.put(minLogLine, line);
                pointers.replace(minLogLine, pointer+1);
            }

        }

    }

    private static void writeAndRemoveLogLine(String fileName, Map<String, String> chunks) {
        System.out.println(chunks.get(fileName));
        chunks.remove(fileName);
    }

    private static String getMinLogLine(Map<String, String> chunks) {
        Map.Entry<String, String> min = Collections.min(chunks.entrySet(), (entry1, entry2) -> {
            ZonedDateTime date1 = ZonedDateTime.parse(entry1.getValue().substring(0, entry1.getValue().indexOf(",")));
            ZonedDateTime date2 = ZonedDateTime.parse(entry2.getValue().substring(0, entry2.getValue().indexOf(",")));
            return date1.compareTo(date2);
        });
        return min.getKey();
    }

    private static String readLine(String fileName, int pointer) {
        try (Stream<String> lines = Files.lines(Paths.get("src/fponce/" + fileName))) {
            return lines.skip(pointer).findFirst().get();
        } catch (IOException | NoSuchElementException e) {
            return null;
        }
    }
}
