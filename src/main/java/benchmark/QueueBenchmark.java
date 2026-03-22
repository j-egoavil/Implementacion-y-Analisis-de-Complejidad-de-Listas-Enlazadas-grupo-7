package benchmark;

import queue.CircularArrayQueue;
import utils.Timer;
import utils.CSVWriter;

import java.util.Random;

public class QueueBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] CHEAP_SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000};
    private static final int[] EXPENSIVE_SIZES = {10, 100, 1_000, 10_000, 50_000, 100_000};
    private static final int[] VERY_EXPENSIVE_SIZES = {10, 100, 1_000, 10_000, 50_000};

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
                    " n=" + n + " avg=" + stats.getAverageNs() +
                    " median=" + stats.getMedianNs()
                );
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long measureOperation(String operationName, int n) {
        return Timer.measure(() -> {
            CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
            Random random = new Random(19L * n + operationName.hashCode());

            switch (operationName) {
                case "enqueue":
                    for (int i = 0; i < n; i++) {
                        queue.enqueue(i);
                    }
                    break;
                case "dequeue":
                    for (int i = 0; i < n; i++) {
                        queue.enqueue(i);
                    }
                    for (int i = 0; i < n; i++) {
                        queue.dequeue();
                    }
                    break;
                case "front":
                    for (int i = 0; i < n; i++) {
                        queue.enqueue(i);
                    }
                    int sampledOps = sampledOps(n);
                    for (int i = 0; i < sampledOps; i++) {
                        queue.front();
                    }
                    break;
                case "delete":
                    for (int i = 0; i < n; i++) {
                        queue.enqueue(i);
                    }
                    int deletions = Math.max(1, Math.min(1_000, n));
                    for (int i = 0; i < deletions; i++) {
                        int target = random.nextInt(n);
                        queue.delete(target);
                        queue.enqueue(target);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operationName);
            }
        });
    }

    private static int sampledOps(int n) {
        return Math.max(1, Math.min(50_000, n));
    }

    private static int[] sizesFor(String operationName) {
        if ("delete".equals(operationName)) {
            return VERY_EXPENSIVE_SIZES;
        }
        // enqueue, dequeue, front are O(1)
        return CHEAP_SIZES;
    }
}