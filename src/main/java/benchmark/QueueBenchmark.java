package benchmark;

import queue.CircularArrayQueue;
import utils.CSVWriter;
import utils.Timer;

public class QueueBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int BATCH_SIZE = BenchmarkRunner.batchSize();
    private static final int[] SIZES = BenchmarkRunner.sizes(BenchmarkRunner.include10Pow8());

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
                BenchmarkStats stats;

                if ("front".equals(operationName)) {
                    stats = benchmarkFront(n);
                } else {
                    stats = benchmarkMutatingOperation(operationName, n);
                }

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

    /**
     * front no modifica la estructura.
     * Reutilizamos la cola base y medimos por lotes para reducir ruido.
     */
    private static BenchmarkStats benchmarkFront(int n) {
        CircularArrayQueue<Integer> queue = buildPreloadedQueue(n);

        return BenchmarkRunner.runBatched(
                () -> Timer.measure(queue::front),
                WARMUP,
                REPETITIONS,
                BATCH_SIZE
        );
    }

    /**
     * Estas operaciones sí modifican la cola.
     * Cada muestra parte de una cola nueva precargada.
     */
    private static BenchmarkStats benchmarkMutatingOperation(String operationName, int n) {
        return BenchmarkRunner.run(
                () -> measureMutatingOperation(operationName, n),
                WARMUP,
                REPETITIONS
        );
    }

    private static long measureMutatingOperation(String operationName, int n) {
        CircularArrayQueue<Integer> queue = buildPreloadedQueue(n);

        switch (operationName) {
            case "enqueue":
                return Timer.measure(() -> queue.enqueue(-1));

            case "dequeue":
                return Timer.measure(queue::dequeue);

            case "delete":
                return Timer.measure(() -> queue.delete(0));

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static CircularArrayQueue<Integer> buildPreloadedQueue(int n) {
        CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
        }
        return queue;
    }

    private static int[] sizesFor(String operationName) {
        return SIZES;
    }
}