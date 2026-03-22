package utils;

import benchmark.BenchmarkStats;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    private final FileWriter writer;

    public CSVWriter(String filePath) throws IOException {
        this(filePath, "size,time");
    }

    public CSVWriter(String filePath, String header) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        writer = new FileWriter(filePath);
        writer.write(header + "\n");
    }

    public void write(int size, long time) throws IOException {
        writer.write(size + "," + time + "\n");
    }

    public void writeStats(int size, BenchmarkStats stats) throws IOException {
        writer.write(
            size + "," +
            stats.getAverageNs() + "," +
            stats.getMedianNs() + "," +
            stats.getMinNs() + "," +
            stats.getMaxNs() + "\n"
        );
    }

    public void writeRaw(String row) throws IOException {
        writer.write(row + "\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}