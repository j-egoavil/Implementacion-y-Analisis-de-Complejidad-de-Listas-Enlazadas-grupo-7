package utils;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    private FileWriter writer;

    public CSVWriter(String filePath) throws IOException {
        writer = new FileWriter(filePath);
        writer.write("size,time\n");
    }

    public void write(int size, long time) throws IOException {
        writer.write(size + "," + time + "\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}