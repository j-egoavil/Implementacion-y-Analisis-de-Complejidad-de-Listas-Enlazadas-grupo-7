package benchmark;

import stack.ArrayStack;
import utils.Timer;
import utils.CSVWriter;

public class StackBenchmark {

    private static final int WARMUP = BenchmarkRunner.warmupRuns();
    private static final int REPETITIONS = BenchmarkRunner.measuredRuns();
    private static final int[] SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};

    private static volatile Object sink;

    public static void runAll() {
        runOperation("push");
        runOperation("pop");
        runOperation("peek");
        runOperation("delete");
    }

    public static void testPush() {
        runOperation("push");
    }

    public static void runSingle(String operationName) {
        switch (operationName) {
            case "push":
            case "pop":
            case "peek":
            case "delete":
                runOperation(operationName);
                return;
            default:
                throw new IllegalArgumentException("Unsupported stack operation: " + operationName);
        }
    }

    private static void runOperation(String operationName) {
        String csvPath = "data/data-stack/stack_" + operationName + ".csv";

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
                        "Stack " + operationName +
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
        ArrayStack<Integer> stack = new ArrayStack<>();
        for (int i = 0; i < n; i++) {
            stack.push(i);
        }

        switch (operationName) {
            case "push":
                return Timer.measure(() -> stack.push(-1));

            case "pop":
                return Timer.measure(() -> sink = stack.pop());

            case "peek":
                return Timer.measure(() -> sink = stack.peek());

            case "delete":
                return Timer.measure(() -> stack.delete(0));

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operationName);
        }
    }

    private static int[] sizesFor(String operationName) {
        return SIZES;
    }
}