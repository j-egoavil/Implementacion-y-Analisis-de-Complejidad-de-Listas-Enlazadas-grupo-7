package benchmark;

import queue.CircularArrayQueue;
import utils.Timer;
import utils.CSVWriter;

public class QueueBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};

    private static volatile Object sink;

    public static void runAll() {
        runOperation("enqueue");
        runOperation("dequeue");
        runOperation("front");
        runOperation("delete");
    }

    public static void testEnqueue() {
        runOperation("enqueue");
    }

    public static void runSingle(String operationName) {
        switch (operationName) {
            case "enqueue":
            case "dequeue":
            case "front":
            case "delete":
                runOperation(operationName);
                return;
            default:
                throw new IllegalArgumentException("Unsupported queue operation: " + operationName);
        }
    }

    private static void runOperation(String operationName) {
        String csvPath = "data/data-queue/queue_" + operationName + ".csv";

        try {
            CSVWriter writer = new CSVWriter(csvPath, "size,avg_time_ns,median_ns,min_ns,max_ns");

            int[] sizes = sizesFor(operationName);
            for (int n : sizes) {
                BenchmarkStats stats = BenchmarkRunner.run(
                        () -> measureOperation(operationName, n),
                        WARMUP,
                        REPETITIONS
                );

                writer.writeStats(n, stats);

                System.out.println(
                        "Queue " + operationName +
                                " n=" + n +
                                " avg=" + stats.getAverageNs() +
                                " median=" + stats.getMedianNs()
                );
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long measureOperation(String operationName, int n) {
        CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
        }

        switch (operationName) {
            case "enqueue":
                return Timer.measure(() -> queue.enqueue(-1));

            case "dequeue":
                return Timer.measure(() -> sink = queue.dequeue());

            case "front":
                return Timer.measure(() -> sink = queue.front());

            case "delete":
                return Timer.measure(() -> queue.delete(0));

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static int[] sizesFor(String operationName) {
        return SIZES;
    }
}